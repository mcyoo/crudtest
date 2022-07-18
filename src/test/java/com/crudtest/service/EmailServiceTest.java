package com.crudtest.service;

import com.crudtest.domain.Email;
import com.crudtest.domain.Post;
import com.crudtest.exception.PostNotFound;
import com.crudtest.repository.EmailRepository;
import com.crudtest.repository.PostRepository;
import com.crudtest.request.EmailCreate;
import com.crudtest.request.PostCreate;
import com.crudtest.request.PostEdit;
import com.crudtest.request.PostSearch;
import com.crudtest.response.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailRepository emailRepository;

    @BeforeEach
    void clean(){
        emailRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void Test1(){
        //given
        EmailCreate emailCreate = EmailCreate.builder()
                .email("dbwptjr@naver.com")
                .build();

        //when
        emailService.emailWrite(emailCreate);

        //then
        assertEquals(1,emailRepository.count());
        Email email = emailRepository.findAll().get(0);
        assertEquals("dbwptjr@naver.com",email.getEmail());
        System.out.println(email.getKey());
    }
}