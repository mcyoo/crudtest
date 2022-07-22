package com.crudtest.repository;

import com.crudtest.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>{
    Optional<User> findByuserKey(String userKey);

    Optional<User> findByEmail(String email);
}
