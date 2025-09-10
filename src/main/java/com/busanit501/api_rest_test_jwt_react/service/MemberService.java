package com.busanit501.api_rest_test_jwt_react.service;

import com.busanit501.api_rest_test_jwt_react.domain.APIUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
public interface MemberService {
    void joinMember(APIUser apiUser, MultipartFile profileImage);
    boolean checkMember(String mid);

}
