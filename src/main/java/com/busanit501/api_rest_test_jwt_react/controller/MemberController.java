package com.busanit501.api_rest_test_jwt_react.controller;

import com.busanit501.api_rest_test_jwt_react.domain.APIUser;
import com.busanit501.api_rest_test_jwt_react.service.MemberService;
import com.busanit501.api_rest_test_jwt_react.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberService memberService;
    private final ProfileService profileService;

    //    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody APIUser apiUser) {
    @PostMapping(value = "/register", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> register(
            @RequestPart("user") APIUser apiUser,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {

        log.info("apiUser확인" + apiUser);
        try {
            memberService.joinMember(apiUser, profileImage);
            return ResponseEntity.ok("회원가입 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // ✅ 중복 아이디 에러 응답
        }
    }

    @GetMapping("/check-mid")
    public ResponseEntity<Boolean> checkMid(@RequestParam String mid) {
        boolean exists = memberService.checkMember(mid);
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(true); // 409 Conflict (중복)
        }
        return ResponseEntity.ok(false); // 200 OK (사용 가능)
    }

    // 프로필 이미지 업로드 API
    @PostMapping("/upload/{mid}")
    public ResponseEntity<String> uploadProfileImage(@PathVariable String mid, @RequestParam("file") MultipartFile file) {
        try {
            String fileId = profileService.uploadProfileImage(mid, file);
            return ResponseEntity.ok(fileId);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    // 프로필 이미지 조회 API
    @GetMapping("/view/{fileId}")
    public ResponseEntity<Resource> viewProfileImage(@PathVariable String fileId) {
        try {
            GridFsResource resource = profileService.getProfileImage(fileId);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(resource.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
