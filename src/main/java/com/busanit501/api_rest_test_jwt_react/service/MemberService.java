package com.busanit501.api_rest_test_jwt_react.service;

import com.busanit501.api_rest_test_jwt_react.domain.APIUser;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MemberService {
    void joinMember(APIUser apiUser);
    boolean checkMember(String mid);

}
