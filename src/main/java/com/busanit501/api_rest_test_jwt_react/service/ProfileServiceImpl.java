package com.busanit501.api_rest_test_jwt_react.service;

import com.busanit501.api_rest_test_jwt_react.domain.APIUser;
import com.busanit501.api_rest_test_jwt_react.repository.APlUserRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final GridFsTemplate gridFsTemplate;
    private final APlUserRepository apiUserRepository;

    @Override
    @Transactional
    public String uploadProfileImage(String mid, MultipartFile file) throws IOException {
        // 1. 사용자 정보 확인
        APIUser user = apiUserRepository.findById(mid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. 메타데이터 생성 (파일명, 콘텐츠 타입 등)
        DBObject metadata = new BasicDBObject();
        metadata.put("contentType", file.getContentType());
        metadata.put("originalFilename", file.getOriginalFilename());

        // 3. GridFS에 파일 저장
        InputStream inputStream = file.getInputStream();
        Object fileId = gridFsTemplate.store(inputStream, file.getOriginalFilename(), file.getContentType(), metadata);

        // 4. MariaDB의 APIUser에 MongoDB 파일 ID 업데이트
        String fileIdString = fileId.toString();
        user.changeProfileImg(fileIdString);
        apiUserRepository.save(user);

        return fileIdString;
    }

    @Override
    public GridFsResource getProfileImage(String fileId) throws IOException {
        // 1. fileId를 이용해 GridFS에서 파일 찾기
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));
        if (file == null) {
            throw new RuntimeException("File not found");
        }

        // 2. GridFS 파일을 리소스로 변환하여 반환
        return gridFsTemplate.getResource(file);
    }
}
