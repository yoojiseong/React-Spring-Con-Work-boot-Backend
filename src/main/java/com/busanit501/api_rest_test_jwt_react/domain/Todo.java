package com.busanit501.api_rest_test_jwt_react.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Table(name = "tbl_todo_api") // 공백 제거
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tno;

    private String title;
    private LocalDate dueDate;
    private String writer;
    private boolean complete;

    public void changeComplete(boolean complete) {
        this.complete = complete;
    }

    public void changeDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void changeTitle(String title) { // 메서드 오류 수정
        this.title = title;
    }
}
