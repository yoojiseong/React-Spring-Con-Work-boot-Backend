package com.busanit501.api_rest_test_jwt_react.dto.stock;

import lombok.Data;
import java.util.List;

@Data
public class StockPredictionRequestDTO {
    private List<List<Double>> data; // [[Open, Low, High, Close], ...] 형식의 리스트
    private String period; // 예측 기간 (예: "1d", "5d", "1mo", "3mo", "6mo", "1y")
}