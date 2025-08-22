package com.busanit501.api_rest_test_jwt_react.security.filter;

import com.busanit501.api_rest_test_jwt_react.security.exception.RefreshTokenException;
import com.busanit501.api_rest_test_jwt_react.util.JWTUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

// 작업 순서19
@Log4j2
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final String refreshPath;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {

        String path = request.getRequestURI();

        // 1. 요청 경로가 refreshPath와 일치하지 않으면 필터 통과
        if (!path.equals(refreshPath)) {
            log.info("Skipping refresh token filter...");
            filterChain.doFilter(request, response);
            return;
        }

        // 2. refreshPath 요청에 대한 처리 로직
        log.info("Refresh Token Filter triggered for path: {}", path);

// 2. 요청에서 accessToken과 refreshToken 추출

        Map<String, String> tokens = parseRequestJSON(request);
        if (tokens == null || !tokens.containsKey("accessToken") || !tokens.containsKey("refreshToken")) {
            log.error("Missing tokens in request.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing accessToken or refreshToken.");
            return;
        }

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        // 이후 로직 추가: Access Token 검증 및 처리
        try {
            // Access Token 검증
            checkAccessToken(accessToken);
        } catch (RefreshTokenException refreshTokenException) {
            // RefreshTokenException 발생 시 응답 에러 전송 및 종료
            refreshTokenException.sendResponseError(response);
            return; // 더 이상 실행하지 않음
        }

        // 이후 로직 추가: Refresh Token 검증 및 처리
        Map<String, Object> refreshClaims = null;

        try {
            // Refresh Token 검증
            refreshClaims = checkRefreshToken(refreshToken);
            log.info("Refresh Token Claims: {}", refreshClaims);
        } catch (RefreshTokenException refreshTokenException) {
            // Refresh Token 검증 실패 시 에러 응답 전송
            refreshTokenException.sendResponseError(response);
            return; // 이후 코드 실행 중단
        }

        // 리플레쉬 토큰이 만료가 되는 시점 계산 하는 부분,
        //Refresh Token의 유효시간이 얼마 남지 않은 경우
        Integer exp = (Integer)refreshClaims.get("exp");

        Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);

        Date current = new Date(System.currentTimeMillis());

        //만료 시간과 현재 시간의 간격 계산
        //만일 3일 미만인 경우에는 Refresh Token도 다시 생성
        long gapTime = (expTime.getTime() - current.getTime());

        log.info("-----------------------------------------");
        log.info("current: " + current);
        log.info("expTime: " + expTime);
        log.info("gap: " + gapTime );

        String username = (String)refreshClaims.get("mid");
        log.info("username: " + username);
        //이상태까지 오면 무조건 AccessToken은 새로 생성
        String accessTokenValue = jwtUtil.generateToken(Map.of("username", username), 1);

        String refreshTokenValue = tokens.get("refreshToken");

        //RefrshToken이 3분도 안남았다면..
//        if(gapTime < (1000 * 60  * 3  ) ){
        //RefrshToken이 3일도 안남았다면..
        if(gapTime < (1000 * 60 * 60 * 24 * 3  ) ){
            log.info("new Refresh Token required...  ");
            refreshTokenValue = jwtUtil.generateToken(Map.of("username", username), 3);
        }

        log.info("Refresh Token result....................");
        log.info("accessToken: " + accessTokenValue);
        log.info("refreshToken: " + refreshTokenValue);
//// 시나리오, 확인
//        1 액세스 토큰 유효함
//        1일
//        eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtaWQiOiJhcGl1c2VyMTAiLCJpYXQiOjE3Mzg2MzY5MDAsImV4cCI6MTczODcyMzMwMH0.W4yv_1H4_2Psl17NWoZSbepe1rVHpnjwSjJrBmnNnrU
//        리플레쉬 토큰  30일
//        eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtaWQiOiJhcGl1c2VyMTAiLCJpYXQiOjE3Mzg2MzY5MDAsImV4cCI6MTc0MTIyODkwMH0.y5PfSgdTBsKwyyX692fgEvsKFchqNFBLuj3fIS0-hhI
//
//        2 리플레쉬 토큰 유효함(초기: 기간30일)
//
//
//, 단, 기간이 3일 미만으로 남았을 경우,
//        리플레쉬 토큰의 만료기간을 변조해서, 1일 정도 남게 설정.
//        eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtaWQiOiJhcGl1c2VyMTAiLCJpYXQiOjE3Mzg2MzY5MDAsImV4cCI6MTczODcyODkwMH0.-Dp_zI0x0XQGU4oBcbYuvpFUNJq0RZ_3HwY56-RJ3AE
//
//        3 새롭게 리플레쉬 토큰 3일 로 발급.
//            eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImFwaXVzZXIxMCIsImlhdCI6MTczODYzNzEwMCwiZXhwIjoxNzM4ODk2MzAwfQ.W3rCCg9GMK9J_xiUms87kSIVdSfDX-8U3fYZ7rQ3nWQ

        sendTokens(accessTokenValue, refreshTokenValue, response);



    }

    /**
     * 요청에서 JSON 데이터를 Map으로 변환
     */
    private Map<String, String> parseRequestJSON(HttpServletRequest request) {
        try (Reader reader = new InputStreamReader(request.getInputStream())) {
            Gson gson = new Gson();
            return gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            log.error("Error reading JSON from request: {}", e.getMessage());
        }
        return null;
    }

    // 액세스 토큰 검사 도구 추가.
    private void checkAccessToken(String accessToken) throws RefreshTokenException {
        try {
            // Access Token 검증
            jwtUtil.validateToken(accessToken);
        } catch (ExpiredJwtException expiredJwtException) {
            // Access Token 만료 시 로그 출력
            log.info("Access Token has expired.");
        } catch (Exception exception) {
            // 기타 검증 실패 시 예외 발생
            log.error("Access Token validation failed: {}", exception.getMessage());
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_ACCESS);
        }
    }

    // 리플레쉬 토큰 검사 도구 또 추가 될 예정.
    private Map<String, Object> checkRefreshToken(String refreshToken) throws RefreshTokenException {
        try {
            // Refresh Token 검증 및 클레임 데이터 반환
            return jwtUtil.validateToken(refreshToken);

        } catch (ExpiredJwtException expiredJwtException) {
            // Refresh Token이 만료된 경우
            log.error("ExpiredJwtException: Refresh Token has expired.");
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH);

        } catch (MalformedJwtException malformedJwtException) {
            // Refresh Token의 형식이 잘못된 경우
            log.error("MalformedJwtException: Invalid Refresh Token format.");
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.BAD_REFRESH);

        } catch (Exception exception) {
            // 기타 예외 발생 시
            log.error("Unexpected exception during Refresh Token validation: {}", exception.getMessage());
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }
        //return null;
    }

    private void sendTokens(String accessTokenValue, String refreshTokenValue, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // 응답 Content-Type 설정

        // JSON 응답 생성
        Gson gson = new Gson();
        String jsonStr = gson.toJson(Map.of(
                "accessToken", accessTokenValue,
                "refreshToken", refreshTokenValue
        ));

        try {
            // 응답 출력
            response.getWriter().println(jsonStr);
        } catch (IOException e) {
            // IOException 발생 시 RuntimeException으로 래핑하여 던짐
            throw new RuntimeException("Failed to send tokens to client", e);
        }
    }
}
