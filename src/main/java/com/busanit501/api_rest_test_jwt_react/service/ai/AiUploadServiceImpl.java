package com.busanit501.api_rest_test_jwt_react.service.ai;

import com.busanit501.api_rest_test_jwt_react.dto.ai.image.AiPredictionResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


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

    // 폴링 관련 상수 설정
    private static final int POLLING_INTERVAL_MS = 2000; // 2초마다 상태 확인
    private static final int MAX_RETRIES = 30; // 최대 30번 재시도 (총 60초)

    @Override
    @Transactional
    public AiPredictionResponseDTO sendImageToDjangoServer(byte[] imageBytes, String filename, int teamNo) throws IOException {

        // 1. 초기 예측 요청 보내기
        AiPredictionResponseDTO initialResponse = sendInitialRequest(imageBytes, filename, teamNo);

        // 2. teamNo가 4 (YOLO)인 경우에만 폴링 로직 실행
        if (teamNo == 4 && initialResponse.getStatusUrl() != null && !initialResponse.getStatusUrl().isEmpty()) {
            log.info("✅ YOLO 요청. 폴링을 시작합니다. Status URL: {}", initialResponse.getStatusUrl());
            try {
                // 폴링을 통해 최종 결과를 받아옴
                return pollForYoloResult(initialResponse.getStatusUrl());
            } catch (TimeoutException e) {
                log.error("❌ 폴링 시간 초과: {}", e.getMessage());
                // 타임아웃 시 적절한 응답을 반환
                AiPredictionResponseDTO errorResponse = new AiPredictionResponseDTO();
                errorResponse.setMessage("YOLO 처리 시간이 초과되었습니다.");
                errorResponse.setStatus("timeout");
                return errorResponse;
            }
        }

        // 3. YOLO가 아닌 경우, 초기 응답을 그대로 반환
        return initialResponse;
    }

    /**
     * Flask 서버에 초기 이미지/동영상 예측을 요청하는 메서드
     */
    private AiPredictionResponseDTO sendInitialRequest(byte[] fileBytes, String filename, int teamNo) throws IOException {
        String flaskUrl;

        // 팀 번호에 따라 URL 분기
        switch (teamNo) {
            case 1:
                flaskUrl = "http://localhost:5000/predict/team1";
                break;
            case 2:
                flaskUrl = "http://localhost:5000/predict/team2";
                break;
            case 3:
                flaskUrl = "http://localhost:5000/predict/team3";
                break;
            case 4:
                // YOLO 예측 요청 경로는 /predict/yolo (가정)
                flaskUrl = "http://localhost:5000/predict/yolo";
                break;
            // case 5번은 status 확인용이므로 초기 요청에서는 사용하지 않음
            default:
                throw new IllegalArgumentException("❌ 지원되지 않는 팀 번호입니다: " + teamNo);
        }
        log.info("🚀 Flask 서버에 초기 요청 전송: {}", flaskUrl);
        log.info("sendInitialRequest filename : " + filename);

        MediaType mediaType = isVideoFile(filename) ? MediaType.parse("video/mp4") : MediaType.parse("image/jpeg");
        RequestBody fileBody = RequestBody.create(fileBytes, mediaType);

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", filename, fileBody)
                .build();

        Request request = new Request.Builder()
                .url(flaskUrl)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            log.info("Flask 초기 응답: {}", responseBody);
            return objectMapper.readValue(responseBody, AiPredictionResponseDTO.class);
        }
    }

    /**
     * YOLO 결과가 나올 때까지 주기적으로 상태를 확인(폴링)하는 메서드
     * @param statusUrl 상태 확인을 위한 URL
     * @return 최종 처리 결과 DTO
     * @throws IOException, TimeoutException
     */
    private AiPredictionResponseDTO pollForYoloResult(String statusUrl) throws IOException, TimeoutException {
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                // 설정된 시간만큼 대기
                Thread.sleep(POLLING_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Polling was interrupted", e);
            }

            log.info("⏳ 상태 확인 시도 {}/{}... URL: {}", i + 1, MAX_RETRIES, statusUrl);

            Request request = new Request.Builder()
                    .url(statusUrl)
                    .get() // 상태 확인은 GET 요청
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    // 서버가 오류를 반환하면 폴링 중단
                    log.error("❌ 상태 확인 중 오류 발생: {}", response);
                    continue; // 다음 재시도
                }

                String responseBody = response.body().string();
                log.info("폴링 응답: {}", responseBody);
                AiPredictionResponseDTO statusResponse = objectMapper.readValue(responseBody, AiPredictionResponseDTO.class);

                // 상태가 "complete"이면 최종 DTO를 반환하고 루프 종료
                if ("complete".equalsIgnoreCase(statusResponse.getStatus())) {
                    log.info("🎉 YOLO 처리 완료!");
                    return statusResponse;
                }
                // 아직 "processing"이면 루프 계속
            }
        }
        // 최대 재시도 횟수를 초과하면 TimeoutException 발생
        throw new TimeoutException("YOLO 결과 확인 시간 초과. " + MAX_RETRIES * POLLING_INTERVAL_MS / 1000 + "초 이상 소요됨.");
    }

    private boolean isVideoFile(String filename) {
        String lowerCaseFilename = filename.toLowerCase();
        return lowerCaseFilename.endsWith(".mp4") || lowerCaseFilename.endsWith(".avi")
                || lowerCaseFilename.endsWith(".mov") || lowerCaseFilename.endsWith(".mkv");
    }
}