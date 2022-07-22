package com.crudtest.repository;

import com.crudtest.domain.Post;
import com.crudtest.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long>, PostRepositoryCustom{
    List<Post> findAllByuserKey(String userKey);


}
