package com.crudtest.controller;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
                .andDo(print());

        //then

    }

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
    @Test
    @DisplayName("/email 요청시 중복된 email은 에러가 발생한다.")
    void test2() throws Exception {

        //글 제목
        //글 내용
        UserCreate request = UserCreate.builder()
                .email("dbwptjr247@naver.com")
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when
        mockMvc.perform(post("/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(post("/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        //then
        assertEquals(1, userRepository.count());
    }
    @Test
    @DisplayName("/email 요청시 이메일 형식이 다르면 에러가 발생한다.")
    void test3() throws Exception {

        //글 제목
        //글 내용
        UserCreate request = UserCreate.builder()
                .email("dbwptjrcom")
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when
        mockMvc.perform(post("/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andDo(print());

    }
}