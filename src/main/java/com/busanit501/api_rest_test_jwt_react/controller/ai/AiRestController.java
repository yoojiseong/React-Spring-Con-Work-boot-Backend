package com.busanit501.api_rest_test_jwt_react.controller.ai;

import com.busanit501.api_rest_test_jwt_react.dto.ai.image.AiPredictionResponseDTO;
import com.busanit501.api_rest_test_jwt_react.service.ai.AiUploadService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/ai")
@Log4j2
public class AiRestController {

    private final AiUploadService aiUploadService;

    @Autowired
    public AiRestController(AiUploadService aiUploadService) {
        this.aiUploadService = aiUploadService;
    }

    @PostMapping("/predict/{teamNo}")
    public AiPredictionResponseDTO uploadImage(
            @PathVariable int teamNo,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        // Django 서버로 이미지 전송 및 응답 처리
        //
        log.info("image 확인 : " + image);
        AiPredictionResponseDTO responseDTO = aiUploadService.sendImageToDjangoServer(image.getBytes(), image.getOriginalFilename(), teamNo);

        // PredictionResponseDTO 객체를 JSON으로 반환
        return responseDTO;
//        return imageUploadService.sendImageToDjangoServer(image.getBytes(), image.getOriginalFilename());
    }


}
