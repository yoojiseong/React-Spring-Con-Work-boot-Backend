package com.busanit501.api_rest_test_jwt_react.controller.ai;

import com.busanit501.api_rest_test_jwt_react.dto.stock.StockDataResponseDTO;
import com.busanit501.api_rest_test_jwt_react.dto.stock.StockPredictionRequestDTO;
import com.busanit501.api_rest_test_jwt_react.dto.stock.StockResultPredictionResponseDTO;
import com.busanit501.api_rest_test_jwt_react.service.ai.StockPredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/ai2")
@RequiredArgsConstructor
@Log4j2
public class AiSamsungStockController {

    private final StockPredictionService stockPredictionService;

    //        "data": [[80000, 81000, 82000, 81500], [82000, 83000, 83500, 83200]],
//                "period": "5d"
//}
//📌 요청 URL: POST http://localhost:8080/api/ai2/predict/rnn
    @PostMapping("/predict/rnn")
    public ResponseEntity<StockResultPredictionResponseDTO> predictWithRNN(@RequestBody StockPredictionRequestDTO requestDTO) throws IOException {
        return ResponseEntity.ok(stockPredictionService.predictWithRNN(requestDTO));
    }

    //    {
//        "data": [[80000, 81000, 82000, 81500], [82000, 83000, 83500, 83200]],
//        "period": "1mo"
//    }
//📌 요청 URL: POST http://localhost:8080/api/ai2/predict/lstm
    @PostMapping("/predict/lstm")
    public ResponseEntity<StockResultPredictionResponseDTO> predictWithLSTM(@RequestBody StockPredictionRequestDTO requestDTO) throws IOException {
        return ResponseEntity.ok(stockPredictionService.predictWithLSTM(requestDTO));
    }

    //    {
//        "data": [[80000, 81000, 82000, 81500], [82000, 83000, 83500, 83200]],
//        "period": "3mo"
//    }
//📌 요청 URL: POST http://localhost:8080/api/ai2/predict/gru
    @PostMapping("/predict/gru")
    public ResponseEntity<StockResultPredictionResponseDTO> predictWithGRU(@RequestBody StockPredictionRequestDTO requestDTO) throws IOException {
        log.info("predictWithGRU requestDTO : {}", requestDTO);
        return ResponseEntity.ok(stockPredictionService.predictWithGRU(requestDTO));
    }

//    ✅ 삼성전자 주식 데이터 조회 (GET)
//📌 요청 URL: GET http://localhost:8080/api/ai2/stock-data?period=5d
    // 예시) period=1d, period=5d, period=1mo, period=3mo, period=6mo, period=1y

    @GetMapping("/stock-data")
    public ResponseEntity<List<StockDataResponseDTO>> getStockData(@RequestParam String period) throws IOException {
        log.info("predictWithGRU period : {}", period);
        return ResponseEntity.ok(stockPredictionService.getStockData(period));
    }
}
