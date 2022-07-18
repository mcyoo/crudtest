package com.crudtest.repository;

import com.crudtest.domain.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email,Long>{
    Optional<Email> findByKey(String key);
}
