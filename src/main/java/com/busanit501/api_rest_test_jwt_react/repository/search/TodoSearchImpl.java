package com.busanit501.api_rest_test_jwt_react.repository.search;

import com.busanit501.api_rest_test_jwt_react.domain.QTodo;
import com.busanit501.api_rest_test_jwt_react.domain.Todo;
import com.busanit501.api_rest_test_jwt_react.dto.PageRequestDTO;
import com.busanit501.api_rest_test_jwt_react.dto.TodoDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

@Log4j2
public class TodoSearchImpl extends QuerydslRepositorySupport implements TodoSearch {

    public TodoSearchImpl() {
        super(Todo.class);
    }

    @Override
    public Page<TodoDTO> list(PageRequestDTO pageRequestDTO) {
        QTodo todo = QTodo.todo;
        JPQLQuery<Todo> query = from(todo);

        // 기간 조건 추가
        if (pageRequestDTO.getFrom() != null && pageRequestDTO.getTo() != null) {
            BooleanBuilder fromToBuilder = new BooleanBuilder();
            fromToBuilder.and(todo.dueDate.goe(pageRequestDTO.getFrom()));
            fromToBuilder.and(todo.dueDate.loe(pageRequestDTO.getTo()));
            query.where(fromToBuilder);
        }

        // 완료 여부 조건 추가
        if (pageRequestDTO.getCompleted() != null) {
            query.where(todo.complete.eq(pageRequestDTO.getCompleted()));
        }

        // 키워드 조건 추가 (제목에 포함 여부)
        if (pageRequestDTO.getKeyword() != null) {
            query.where(todo.title.contains(pageRequestDTO.getKeyword()));
        }

        // 페이징 적용 (정렬 기준을 "tno"로 지정)
        this.getQuerydsl().applyPagination(pageRequestDTO.getPageable("tno"), query);

        // Todo 엔티티를 TodoDTO로 변환
        JPQLQuery<TodoDTO> dtoQuery = query.select(Projections.bean(
                TodoDTO.class,
                todo.tno,
                todo.title,
                todo.dueDate,
                todo.complete,
                todo.writer
        ));

        List<TodoDTO> list = dtoQuery.fetch();
        Long count = dtoQuery.fetchCount();

        return new PageImpl<>(list, pageRequestDTO.getPageable("tno"), count);
    }
}
