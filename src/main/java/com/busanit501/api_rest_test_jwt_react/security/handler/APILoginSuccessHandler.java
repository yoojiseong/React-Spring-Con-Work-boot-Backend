package com.busanit501.api_rest_test_jwt_react.security.handler;

import com.busanit501.api_rest_test_jwt_react.domain.APIUser;
import com.busanit501.api_rest_test_jwt_react.dto.APIUserDTO;
import com.busanit501.api_rest_test_jwt_react.repository.APlUserRepository;
import com.busanit501.api_rest_test_jwt_react.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//작업 순서14
@Log4j2
@RequiredArgsConstructor
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws ServletException, IOException {
        // 로그인 성공 시 동작 로직
        log.info("Login Success Handler triggered");

        // TODO: JWT 생성 및 응답으로 반환
        // 응답 Content-Type 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 인증 정보 로그 출력
        log.info("Authentication: {}", authentication);
        log.info("Authenticated Username: {}", authentication.getName());

        // 클레임 설정 (사용자 이름 포함)
//        Map<String, Object> claims = Map.of("mid", authentication.getName());

        Map<String, Object> claims = new HashMap<>();
        claims.put("mid", authentication.getName()); // 또는 authentication.getName()

        // Access Token 유효기간: 1일
        String accessToken = jwtUtil.generateToken(claims, 1);

        // Refresh Token 유효기간: 30일
        String refreshToken = jwtUtil.generateToken(claims, 30);

        // JSON 형식 응답 생성
        Gson gson = new Gson();
//        Map<String, String> keyMap = Map.of(
//                "accessToken", accessToken,
//                "refreshToken", refreshToken
//        );

        // 응답으로 보낼 데이터를 담을 가변 맵으로 변경
        Map<String, Object> keyMap = new HashMap<>();

        Object principal = authentication.getPrincipal();

// 1. Principal의 실제 클래스 타입을 확인합니다.
        log.info("Principal's actual class: {}", principal.getClass().getName());
        if (principal instanceof APIUserDTO) {
            APIUserDTO userDTO = (APIUserDTO) principal;
            String profileImg = userDTO.getProfileImg();

            // 2. DTO 객체와 profileImg 값을 직접 확인합니다.
            log.info("Casting to APIUserDTO successful. DTO userDTO: {}", userDTO);
            log.info("ProfileImg value from DTO: profileImg : {}", profileImg);

            if (profileImg != null) {
                keyMap.put("profileImg", profileImg);
                log.info("profileImg claim added successfully.");
            } else {
                log.warn("profileImg is null. Claim not added.");
            }
        } else {
            // 3. instanceof 검사가 실패한 경우 로그를 남깁니다.
            log.warn("Principal is NOT an instance of APIUserDTO. Cannot add profileImg claim.");
        }
        keyMap.put("accessToken", accessToken);
        keyMap.put("refreshToken", refreshToken);

        String jsonStr = gson.toJson(keyMap);

        // 응답으로 JSON 전송
        response.getWriter().println(jsonStr);
    }
}
