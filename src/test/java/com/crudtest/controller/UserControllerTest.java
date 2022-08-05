package com.crudtest.controller;

import com.crudtest.domain.User;
import com.crudtest.repository.UserRepository;
import com.crudtest.request.UserCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.crudtest.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("/email 요청시 email 형식이 아니면 에러가 발생한다.")
    void test() throws Exception {

        //글 제목
        //글 내용
        UserCreate request = UserCreate.builder()
                .email("이메일")
                .build();

        String json = objectMapper.writeValueAsString(request);

        System.out.println(json);

        //when
        mockMvc.perform(post("/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.email").value("잘못된 이메일 형식입니다."))
                .andDo(print());

        //then

    }

    /* 이메일 요청 확인
    @Test
    @DisplayName("/email 요청시 이메일이 전송된다.")
    void test1() throws Exception {

        //글 제목
        //글 내용
        UserCreate request = UserCreate.builder()
                .email("dbwptjr247@naver.com")
                .build();

        String json = objectMapper.writeValueAsString(request);

        System.out.println(json);

        //when
        mockMvc.perform(post("/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andDo(print());

        //then
        assertEquals(1, userRepository.count());
    }
    */
    @Test
    @DisplayName("/email 요청시 중복된 email은 에러가 발생한다.")
    void test2() throws Exception {

        //글 제목
        //글 내용
        UserCreate request = UserCreate.builder()
                .email("dbwptjr247@naver.com")
                .build();

        String json = objectMapper.writeValueAsString(request);

        User user = User.builder()
                .email("dbwptjr247@naver.com")
                .userKey("123456")
                .build();
        //when
        userRepository.save(user);

        mockMvc.perform(post("/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.email").value("이미 등록된 이메일입니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("/email 요청시 40자를 넘어가면 에러가 발생한다.")
    void test4() throws Exception {

        //글 제목
        //글 내용
        UserCreate request = UserCreate.builder()
                .email("123456741252451425142512359asasdfdfasdf012345@naver.com")
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when
        mockMvc.perform(post("/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())//400
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.email").value("이메일이 40자를 초과할 수 없습니다."))
                .andDo(print());

    }
}