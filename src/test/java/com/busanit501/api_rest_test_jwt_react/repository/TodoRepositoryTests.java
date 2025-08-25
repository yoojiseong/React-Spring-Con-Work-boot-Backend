package com.busanit501.api_rest_test_jwt_react.repository;

import com.busanit501.api_rest_test_jwt_react.domain.Todo;
import com.busanit501.api_rest_test_jwt_react.dto.PageRequestDTO;
import com.busanit501.api_rest_test_jwt_react.dto.TodoDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class TodoRepositoryTests {

    @Autowired
    private TodoRepository todoRepository;

    @Test
    public void testInsert() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Todo todo = Todo.builder()
                    .title("Todo..." + i)
                    .dueDate(LocalDate.of(2025, (i % 12) + 1, (i % 28) + 1)) // 일(day) 값 수정
                    .writer("user" + (i % 10))
                    .complete(false)
                    .build();

            todoRepository.save(todo);
            log.info("Inserted: " + todo);
        });
    }

    @Test
    public void testSearch() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .from(LocalDate.of(2025, 10, 1))
                .to(LocalDate.of(2025, 12, 31))
                .build();

        Page<TodoDTO> result = todoRepository.list(pageRequestDTO);
        result.forEach(todoDTO -> log.info(todoDTO.toString()));
    }
}