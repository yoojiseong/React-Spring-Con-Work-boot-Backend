package com.busanit501.api_rest_test_jwt_react.security.filter;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

//작업 순서12
@Log4j2
public class APILoginFilter extends AbstractAuthenticationProcessingFilter {

    // 생성자: 기본 필터 URL 설정
    public APILoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, ServletException {
        // 로깅: 필터 동작 확인
        log.info("APILoginFilter - attemptAuthentication executed");

        // 인증 로직 미구현 상태
        // GET 요청은 지원하지 않음
        // 1번 검사, get 요청은 무시
        if (request.getMethod().equalsIgnoreCase("GET")) {
            log.info("GET METHOD NOT SUPPORTED");
            return null;
        }

        // JSON 데이터 파싱
        // parseRequestJSON: JSON 문자열 -> 자바 Map 형태로 변환,
        // Map 안에 검사해야하는 mid, mpw 포함되어있음.
        Map<String, String> jsonData = parseRequestJSON(request);
        log.info("Parsed JSON Data: {}", jsonData);

        // TODO: 인증 로직 추가
        // JSON 데이터에서 사용자 ID와 비밀번호를 추출하여 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        jsonData.get("mid"), // 사용자 ID
                        jsonData.get("mpw")  // 사용자 비밀번호
                );

        // AuthenticationManager를 사용하여 인증 시도
        return getAuthenticationManager().authenticate(authenticationToken);

    }

    private Map<String, String> parseRequestJSON(HttpServletRequest request) {
        // JSON 데이터를 파싱하여 mid와 mpw 값을 Map으로 처리
        try (Reader reader = new InputStreamReader(request.getInputStream())) {
            Gson gson = new Gson();
            return gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            log.error("Error parsing JSON request: {}", e.getMessage());
        }
        return null;
    }
}
