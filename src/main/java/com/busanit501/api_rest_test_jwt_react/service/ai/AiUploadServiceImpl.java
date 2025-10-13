package com.busanit501.api_rest_test_jwt_react.service.ai;

import com.busanit501.api_rest_test_jwt_react.dto.ai.image.AiPredictionResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Log4j2
public class AiUploadServiceImpl implements AiUploadService {

    //    private final OkHttpClient client = new OkHttpClient();
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS) // ✅ 연결 타임아웃
            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)    // ✅ 읽기 타임아웃
            .writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)   // ✅ 쓰기 타임아웃
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper

    @Override
    @Transactional
    public AiPredictionResponseDTO sendImageToDjangoServer(byte[] imageBytes, String filename, int teamNo) throws IOException {

        String djangoUrl;

        // ✅ 팀 번호에 따라 URL 분기
        switch (teamNo) {
            case 1:
                djangoUrl = "http://localhost:5000/classify";
                break;
            case 2:
                djangoUrl = "http://localhost:5000/predict/team2";
                break;
            case 3:
                djangoUrl = "http://localhost:5000/predict/team3";
                break;
            case 4:
                djangoUrl = "http://localhost:5000/predict/yolo";
                break;
            default:
                throw new IllegalArgumentException("❌ 지원되지 않는 팀 번호입니다: " + teamNo);
        }
        log.info("🚀 Django 서버에 요청 전송: {}", djangoUrl);

        log.info("sendImageToDjangoServer filename : " + filename);

        // ✅ 파일 확장자 확인 (이미지 vs 동영상)
        MediaType mediaType = isVideoFile(filename) ? MediaType.parse("video/mp4") : MediaType.parse("image/jpeg");

        // 이미지 파일을 MultipartBody로 구성
        RequestBody fileBody = RequestBody.create(imageBytes, mediaType);

        // Multipart request body
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", filename, fileBody) // ✅ 동영상인 경우 "video" 필드 사용
                .build();

        // Request 객체 생성
        Request request = new Request.Builder()
                .url(djangoUrl)
                .post(requestBody)
                .build();

        // 요청 실행
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // 응답 바디를 String으로 읽기
            String responseBody = response.body().string();
            log.info("responseBody : " + responseBody);

            // 응답을 PredictionResponseDTO 객체로 변환
            return objectMapper.readValue(responseBody, AiPredictionResponseDTO.class);
        }
    }

    private boolean isVideoFile(String filename) {
        String lowerCaseFilename = filename.toLowerCase();
        return lowerCaseFilename.endsWith(".mp4") || lowerCaseFilename.endsWith(".avi")
                || lowerCaseFilename.endsWith(".mov") || lowerCaseFilename.endsWith(".mkv");
    }
}