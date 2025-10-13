package com.busanit501.api_rest_test_jwt_react.service.ai;

import com.busanit501.api_rest_test_jwt_react.dto.ai.image.AiPredictionResponseDTO;

import java.io.IOException;

public interface AiUploadService {
    AiPredictionResponseDTO sendImageToDjangoServer(byte[] imageBytes, String filename, int teamNo) throws IOException;
}
