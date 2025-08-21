package com.busanit501.api_rest_test_jwt_react.repository;

import com.busanit501.api_rest_test_jwt_react.domain.APIUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//작업 순서8
@Repository
public interface APlUserRepository extends JpaRepository<APIUser, String> {
}
