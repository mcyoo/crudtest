package com.crudtest.service;

import com.crudtest.domain.User;
import com.crudtest.exception.InvalidRequest;
import com.crudtest.exception.PostNotFound;
import com.crudtest.repository.UserRepository;
import com.crudtest.request.UserCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clean(){
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("이메일 저장")
    void Test1(){
        //given
        UserCreate emailCreate = UserCreate.builder()
                .email("dbwptjr@naver.com")
                .build();

        //when
        userService.emailWrite(emailCreate);

        //then
        assertEquals(1, userRepository.count());
        User user = userRepository.findAll().get(0);
        assertEquals("dbwptjr@naver.com", user.getEmail());
        System.out.println(user.getKey());
    }

    @Test
    @DisplayName("이메일 중복시 저장 안함")
    void Test2(){
        //given
        User user = User.builder()
                .email("dbwptjr@naver.com")
                .key("123123")
                .build();
        userRepository.save(user);

        UserCreate emailCreate = UserCreate.builder()
                .email("dbwptjr@naver.com")
                .build();


        // expected
        assertThrows(InvalidRequest.class,()->{
            userService.emailWrite(emailCreate);
        });
    }
}