package com.busanit501.api_rest_test_jwt_react.dto.ai.image;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)  // ✅ 예기치 않은 필드는 무시
public class AiPredictionResponseDTO {

    @JsonProperty("prediction")  // ✅ Flask 응답의 "prediction" 필드 추가
    private String prediction;

    private String filename; // Flask 응답의 filename 필드

    @JsonProperty("confidence")
    private String confidence; // 예측에 대한 신뢰도 (문자열로 변경, e.g., "95.00%")

    @JsonProperty("class_index")
    private int classIndex; // Flask 응답의 predicted_class 필드 매핑

    @JsonProperty("predicted_class")
    private String predictedClass; // Flask 응답의 predicted_class 필드

    // ✅ Flask 응답의 이미지 또는 동영상 파일 URL
    @JsonProperty("file_url")
    private String fileUrl;  // YOLO 결과물의 미리보기 URL

    @JsonProperty("download_url")
    private String downloadUrl; // YOLO 결과물의 다운로드 URL

    @JsonProperty("message")
    private String message; // YOLO 결과물의 message

    @JsonProperty("request_id")
    private String requestId; // YOLO 결과물의 requestId

    @JsonProperty("file_type")
    private String fileType; // YOLO 결과물의 file_type

    @JsonProperty("status_url")
    private String statusUrl; // YOLO 결과물의 status_url

    @JsonProperty("s3_url")
    private String s3Url; // YOLO 결과물의 status_url

    @JsonProperty("original_url")
    private String originalUrl; // YOLO 결과물의 status_url

}
