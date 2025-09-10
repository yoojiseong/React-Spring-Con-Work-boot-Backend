package com.busanit501.api_rest_test_jwt_react.service;

import com.busanit501.api_rest_test_jwt_react.domain.APIUser;
import com.busanit501.api_rest_test_jwt_react.repository.APlUserRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final PasswordEncoder passwordEncoder;
    private final APlUserRepository apiUserRepository;
    private final GridFsTemplate gridFsTemplate; // ✅ GridFS 주입


    @Override
    public void joinMember(APIUser apiUser, MultipartFile profileImage) {
        // 🔹 아이디 중복 확인
        if (apiUserRepository.existsByMid(apiUser.getMid())) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        String profileImgId = null; // ✅ GridFS 파일 ID를 저장할 변수

        // ✅ 프로필 이미지가 존재할 경우, GridFS에 저장하는 로직
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                DBObject metadata = new BasicDBObject();
                metadata.put("contentType", profileImage.getContentType());
                Object fileId = gridFsTemplate.store(
                        profileImage.getInputStream(),
                        profileImage.getOriginalFilename(),
                        profileImage.getContentType(),
                        metadata
                );
                profileImgId = fileId.toString();
            } catch (IOException e) {
                // 실제 프로덕션 코드에서는 더 구체적인 예외 처리가 필요합니다.
                throw new RuntimeException("프로필 이미지 저장에 실패했습니다.", e);
            }
        }

        APIUser apiUser2 = APIUser.builder()
                .mid(apiUser.getMid())
                .mpw(passwordEncoder.encode(apiUser.getMpw())) // 비밀번호 암호화
                .profileImg(profileImgId) // ✅ 저장된 이미지 파일 ID를 설정
                .build();
        apiUserRepository.save(apiUser2);
    }

    @Override
    public boolean checkMember(String mid) {
        boolean check = apiUserRepository.existsByMid(mid);
        return check;
    }
}
