package com.busanit501.api_rest_test_jwt_react.repository;

import com.busanit501.api_rest_test_jwt_react.domain.APIUser;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.IntStream;

//작업 순서9
@SpringBootTest
@Log4j2
public class APIUserRepositoryTests {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private APlUserRepository apiUserRepository;

    @Test
    public void testInserts() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            // APIUser 객체 생성
            APIUser apiUser = APIUser.builder()
                    .mid("apiuser" + i) // 사용자 ID 설정
                    .mpw(passwordEncoder.encode("11111")) // 비밀번호 암호화
                    .build();

            // 데이터 저장
            apiUserRepository.save(apiUser);

            // 로그 출력
            log.info("Saved APIUser: " + apiUser);
        });
    }
}
