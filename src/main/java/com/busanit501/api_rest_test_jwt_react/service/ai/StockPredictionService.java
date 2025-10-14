package com.busanit501.api_rest_test_jwt_react.service.ai;



import com.busanit501.api_rest_test_jwt_react.dto.stock.StockDataResponseDTO;
import com.busanit501.api_rest_test_jwt_react.dto.stock.StockPredictionRequestDTO;
import com.busanit501.api_rest_test_jwt_react.dto.stock.StockResultPredictionResponseDTO;

import java.io.IOException;
import java.util.List;

public interface StockPredictionService {
    StockResultPredictionResponseDTO predictWithRNN(StockPredictionRequestDTO requestDTO) throws IOException;
    StockResultPredictionResponseDTO predictWithLSTM(StockPredictionRequestDTO requestDTO) throws IOException;
    StockResultPredictionResponseDTO predictWithGRU(StockPredictionRequestDTO requestDTO) throws IOException;
    List<StockDataResponseDTO> getStockData(String period) throws IOException;
}