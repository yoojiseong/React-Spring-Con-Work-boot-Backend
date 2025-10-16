package com.busanit501.api_rest_test_jwt_react.dto.ai.image;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiPredictionResponseDTO {

    // ✅ Flask의 상태 응답을 받기 위한 필드 추가
    @JsonProperty("status")
    private String status;

    @JsonProperty("prediction")
    private String prediction;

    private String filename;

    @JsonProperty("confidence")
    private String confidence;

    @JsonProperty("class_index")
    private int classIndex;

    @JsonProperty("predicted_class")
    private String predictedClass;

    // ✅ Flask의 상태 응답에서는 'url' 필드로 결과 파일 경로가 올 수 있음
    @JsonProperty("url")
    private String fileUrl;

    @JsonProperty("download_url")
    private String downloadUrl;

    @JsonProperty("message")
    private String message;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("file_type")
    private String fileType;

    @JsonProperty("status_url")
    private String statusUrl;

    @JsonProperty("s3_url")
    private String s3Url;

    @JsonProperty("original_url")
    private String originalUrl;
}
