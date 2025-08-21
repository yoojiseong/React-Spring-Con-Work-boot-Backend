package com.busanit501.api_rest_test_jwt_react.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

//작업 순서7
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class APIUser {

    @Id // Primary Key 설정
    private String mid; // 사용자 ID

    private String mpw; // 사용자 비밀번호

    // 비밀번호 변경 메서드
    public void changePw(String mpw) {
        this.mpw = mpw;
    }
}
