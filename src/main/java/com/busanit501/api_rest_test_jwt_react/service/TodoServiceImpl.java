package com.busanit501.api_rest_test_jwt_react.service;

import com.busanit501.api_rest_test_jwt_react.domain.Todo;
import com.busanit501.api_rest_test_jwt_react.dto.PageRequestDTO;
import com.busanit501.api_rest_test_jwt_react.dto.PageResponseDTO;
import com.busanit501.api_rest_test_jwt_react.dto.TodoDTO;
import com.busanit501.api_rest_test_jwt_react.repository.TodoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Long register(TodoDTO todoDTO) {
        Todo todo = modelMapper.map(todoDTO, Todo.class); // 오타 수정
        Long tno = todoRepository.save(todo).getTno(); // getTno() 오타 수정
        return tno;
    }

    @Override
    public TodoDTO read(Long tno) {
        Optional<Todo> result = todoRepository.findById(tno);
        Todo todo = result.orElseThrow(); // 예외 발생 시 자동으로 NoSuchElementException 던짐
        return modelMapper.map(todo, TodoDTO.class);
    }

    @Override
    public PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO) {
        Page<TodoDTO> result = todoRepository.list(pageRequestDTO);
        return PageResponseDTO.<TodoDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.toList())
                .total((int) result.getTotalElements())
                .build();
    }
    @Override
    public void remove(Long tno) {
        todoRepository.deleteById(tno);
    }

    @Override
    public void modify(TodoDTO todoDTO) {
        Optional<Todo> result = todoRepository.findById(todoDTO.getTno());
        Todo todo = result.orElseThrow();

        todo.changeTitle(todoDTO.getTitle());
        todo.changeDueDate(todoDTO.getDueDate());
        todo.changeComplete(todoDTO.isComplete());

        todoRepository.save(todo);
    }

}
