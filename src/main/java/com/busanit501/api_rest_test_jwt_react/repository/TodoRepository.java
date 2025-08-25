package com.busanit501.api_rest_test_jwt_react.repository;

import com.busanit501.api_rest_test_jwt_react.domain.Todo;
import com.busanit501.api_rest_test_jwt_react.repository.search.TodoSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>, TodoSearch {
}
