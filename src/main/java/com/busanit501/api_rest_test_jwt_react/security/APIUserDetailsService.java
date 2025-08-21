package com.busanit501.api_rest_test_jwt_react.security;

import com.busanit501.api_rest_test_jwt_react.domain.APIUser;
import com.busanit501.api_rest_test_jwt_react.dto.APIUserDTO;
import com.busanit501.api_rest_test_jwt_react.repository.APlUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//작업 순서10
@Service
@Log4j2
@RequiredArgsConstructor
public class APIUserDetailsService implements UserDetailsService {

    // Repository 주입
    private final APlUserRepository apiUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 데이터베이스에서 사용자 정보 조회
        // 사용자 정보 조회
        Optional<APIUser> result = apiUserRepository.findById(username);

        // 사용자 정보가 없을 경우 예외 처리
        APIUser apiUser = result.orElseThrow(() ->
                new UsernameNotFoundException("Cannot find user with username: " + username)
        );

        // 사용자 정보 로깅
        log.info("APIUserDetailsService - Found APIUser: {}", apiUser);

        // APIUserDTO 생성
        APIUserDTO dto = new APIUserDTO(
                apiUser.getMid(), // 사용자 ID
                apiUser.getMpw(), // 사용자 비밀번호
                List.of(new SimpleGrantedAuthority("ROLE_USER")) // 권한 설정
//                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")) // 권한 설정
        );

        // DTO 정보 로깅
        log.info("APIUserDetailsService - Created APIUserDTO: {}", dto);

        // UserDetails 반환
        return dto;
    }
}
