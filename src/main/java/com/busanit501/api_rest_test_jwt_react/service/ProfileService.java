package com.busanit501.api_rest_test_jwt_react.service;

import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileService {
    // 프로필 이미지 업로드 (성공 시 MongoDB 파일 ID 반환)
    String uploadProfileImage(String mid, MultipartFile file) throws IOException;

    // 프로필 이미지 조회 (GridFS 파일과 리소스 반환)
    GridFsResource getProfileImage(String fileId) throws IllegalStateException, IOException;
}