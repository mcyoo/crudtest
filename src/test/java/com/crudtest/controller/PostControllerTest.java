package com.crudtest.controller;

import com.crudtest.domain.User;
import com.crudtest.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.crudtest.domain.Post;
import com.crudtest.repository.PostRepository;
import com.crudtest.request.PostCreate;
import com.crudtest.request.PostEdit;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

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
        postRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("/posts 요청시 Hello World 를 출력한다.")
    void test() throws Exception {
        //글 제목
        //글 내용

        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다")
                .build();

        User user = User.builder()
                .email("asdf")
                .userKey("123456")
                .build();

        userRepository.save(user);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/posts?key={userKey}", user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.postId").value(1))
                //.andExpect(content().string("{postId : 1}"))
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 title 값은 필수다.")
    void test2() throws Exception {
        //글 제목
        //글 내용
        PostCreate request = PostCreate.builder()
                .content("내용입니다")
                .build();


        User user = User.builder()
                .email("asdf")
                .userKey("123456")
                .build();

        userRepository.save(user);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/posts?key={userKey}", user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())//400
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 DB에 값이 저장된다.")
    void test3() throws Exception {
        //글 제목
        //글 내용
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        User user = User.builder()
                .email("asdf")
                .userKey("123456")
                .build();

        userRepository.save(user);

        //when
        mockMvc.perform(post("/posts?key={userKey}", user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());

        //then
        assertEquals(1, postRepository.count());

        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다.", post.getTitle());
        assertEquals("내용입니다.", post.getContent());
        assertEquals(0, post.getGood_count());
        assertEquals(0, post.getBad_count());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test4() throws Exception {
        //given
        Post post = Post.builder()
                .title("1234567890")
                .content("bar")
                .userKey("1234")
                .build();
        postRepository.save(post);

        User user = User.builder()
                .email("asdf")
                .userKey("1234")
                .build();

        userRepository.save(user);

        //expected
        mockMvc.perform(get("/posts/{postId}?key={userKey}", post.getId(), user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("1234567890"))
                .andExpect(jsonPath("$.content").value("bar"))
                .andExpect(jsonPath("$.good_count").value(0))
                .andExpect(jsonPath("$.bad_count").value(0))
                .andDo(print());
    }

    @Test
    @DisplayName("글 여러개 조회")
    void test5() throws Exception {
        //given
        List<Post> requestPosts = IntStream.range(1, 21)
                .mapToObj(i -> Post.builder()
                        .title("제목 " + i)
                        .content("내용 " + i)
                        .userKey("1234")
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);


        User user = User.builder()
                .email("asdf")
                .userKey("1234")
                .build();

        userRepository.save(user);

        //expected
        mockMvc.perform(get("/posts?page=1&size=10&key={userKey}", user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(10)))
                .andExpect(jsonPath("$[0].title").value("제목 20"))
                .andExpect(jsonPath("$[0].content").value("내용 20"))
                .andExpect(jsonPath("$[0].good_count").value(0))
                .andExpect(jsonPath("$[0].bad_count").value(0))
                .andDo(print());
    }

    @Test
    @DisplayName("글 수정")
    void test6() throws Exception {
        //given
        Post post = postRepository.save(Post.builder()
                .title("1")
                .content("1")
                .userKey("1234")
                .build()
        );
        PostEdit postEdit = PostEdit.builder()
                .title("2")
                .content("2")
                .build();

        String json = objectMapper.writeValueAsString(postEdit);

        User user = User.builder()
                .email("asdf")
                .userKey("1234")
                .build();

        userRepository.save(user);

        //expected
        mockMvc.perform(patch("/posts/{postId}?key={userKey}", post.getId(), user.getUserKey())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 삭제")
    void test7() throws Exception {
        //given
        Post post = postRepository.save(Post.builder()
                .title("1")
                .content("1")
                        .userKey("1234")
                .build()
        );

        User user = User.builder()
                .email("asdf")
                .userKey("1234")
                .build();

        userRepository.save(user);

        //expected
        mockMvc.perform(delete("/posts/{postId}?key={userKey}", post.getId(), user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void test8() throws Exception {
        User user = User.builder()
                .email("asdf")
                .userKey("1234")
                .build();

        userRepository.save(user);
        //expected
        mockMvc.perform(get("/posts/{postId}?key={userKey}", 1L, user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 글입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정")
    void test9() throws Exception {
        //given
        PostEdit postEdit = PostEdit.builder()
                .title("2")
                .content("2")
                .build();

        String json = objectMapper.writeValueAsString(postEdit);

        User user = User.builder()
                .email("asdf")
                .userKey("1234")
                .build();

        userRepository.save(user);

        //expected
        mockMvc.perform(patch("/posts/{postId}?key={userKey}", 1L, user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 글입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 수정시 제목은 필수 이다.")
    void test10() throws Exception {
        //given
        Post post = postRepository.save(Post.builder()
                .title("1")
                .content("1")
                .userKey("1234")
                .build()
        );
        PostEdit postEdit = PostEdit.builder()
                .content("2")
                .build();

        String json = objectMapper.writeValueAsString(postEdit);

        User user = User.builder()
                .email("asdf")
                .userKey("1234")
                .build();

        userRepository.save(user);

        //expected
        mockMvc.perform(patch("/posts/{postId}?key={userKey}", post.getId(), user.getUserKey())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("제목은 필수 입니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("게시글 수정시 제목은 필수 이다. 공백값 전송")
    void test11() throws Exception {
        //given
        Post post = postRepository.save(Post.builder()
                .title("1")
                .content("1")
                .userKey("1234")
                .build()
        );
        PostEdit postEdit = PostEdit.builder()
                .title("")
                .content("2")
                .build();

        String json = objectMapper.writeValueAsString(postEdit);

        User user = User.builder()
                .email("dbwptjr247@naver.com")
                .userKey("1234")
                .build();

        userRepository.save(user);

        //expected
        mockMvc.perform(patch("/posts/{postId}?key={userKey}", post.getId(), user.getUserKey())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("제목은 필수 입니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("게시글 작성시 내용은 필수이다. 공백값 전송")
    void test12() throws Exception {
        //글 제목
        //글 내용
        PostCreate request = PostCreate.builder()
                .title("제석")
                .content("")
                .build();

        String json = objectMapper.writeValueAsString(request);

        User user = User.builder()
                .email("asdf")
                .userKey("123456")
                .build();

        userRepository.save(user);

        //when
        mockMvc.perform(post("/posts?key={userKey}", user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.content").value("내용은 필수 입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 userKey 값이 있어야 한다.")
    void test13() throws Exception {
        //글 제목
        //글 내용
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();


        User user = User.builder()
                .email("asdf")
                .userKey("123456")
                .build();

        userRepository.save(user);

        String json = objectMapper.writeValueAsString(request);
        String userKey = "123456";

        //when
        mockMvc.perform(post("/posts?key={userKey}",userKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());

        //then
        assertEquals(1, postRepository.count());

        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다.", post.getTitle());
        assertEquals("내용입니다.", post.getContent());
    }
    @Test
    @DisplayName("/posts 수정 요청 시 userKey 값이 다르면 권한 오류가 나온다.")
    void test14() throws Exception {

        //given
        Post post1 = postRepository.save(Post.builder()
                .title("1")
                .content("1")
                .userKey("1234")
                .build()
        );
        Post post2 = postRepository.save(Post.builder()
                .title("1")
                .content("1")
                .userKey("123456")
                .build()
        );

        User user = User.builder()
                .email("asdf")
                .userKey("1234")
                .build();
        userRepository.save(user);

        PostEdit postEdit = PostEdit.builder()
                .title("2")
                .content("2")
                .build();

        String json = objectMapper.writeValueAsString(postEdit);

        //expected
        mockMvc.perform(patch("/posts/{postId}?key={userKey}", post2.getId(), user.getUserKey())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("권한이 없습니다."))
                .andExpect(jsonPath("$.validation.id").value("수정할 권한이 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("글 삭제시 userKey 값이 다르면 권한 오류가 난다.")
    void test15() throws Exception {
        //given
        Post post = postRepository.save(Post.builder()
                .title("1")
                .content("1")
                .userKey("123456")
                .build()
        );

        User user = User.builder()
                .email("asdf")
                .userKey("1234")
                .build();

        userRepository.save(user);

        //expected
        mockMvc.perform(delete("/posts/{postId}?key={userKey}", post.getId(), user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("권한이 없습니다."))
                .andExpect(jsonPath("$.validation.id").value("삭제할 권한이 없습니다."))
                .andDo(print());
    }
    @Test
    @DisplayName("/posts 요청시 title 값이 20자를 넘으면 안된다.")
    void test16() throws Exception {
        //글 제목
        //글 내용
        PostCreate request = PostCreate.builder()
                .title("1234567890123456789012345")
                .content("내용입니다")
                .build();


        User user = User.builder()
                .email("asdf")
                .userKey("123456")
                .build();

        userRepository.save(user);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/posts?key={userKey}", user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())//400
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("제목이 20자를 초과할 수 없습니다."))
                .andDo(print());
    }
    @Test
    @DisplayName("/posts 수정 요청 시 title 값이 20자를 초과하면 에러가 발생한다.")
    void test17() throws Exception {

        //given
        Post post = postRepository.save(Post.builder()
                .title("1")
                .content("1")
                .userKey("1234")
                .build()
        );

        User user = User.builder()
                .email("asdf")
                .userKey("1234")
                .build();
        userRepository.save(user);

        PostEdit postEdit = PostEdit.builder()
                .title("123456789012345678901")
                .content("21234")
                .build();

        String json = objectMapper.writeValueAsString(postEdit);

        //expected
        mockMvc.perform(patch("/posts/{postId}?key={userKey}", post.getId(), user.getUserKey())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())//400
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("제목이 20자를 초과할 수 없습니다."))
                .andDo(print());
    }
    @Test
    @DisplayName("/posts/id/good post good 카운터가 증가한다.")
    void test19() throws Exception {

        //given
        Post post = postRepository.save(Post.builder()
                .title("1")
                .content("1")
                .userKey("1234")
                .build()
        );

        User user = User.builder()
                .email("asdf")
                .userKey("1234")
                .build();
        userRepository.save(user);

        //expected
        mockMvc.perform(post("/posts/{postId}/good_count?key={userKey}", post.getId(), user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())//200
                .andExpect(jsonPath("$.good_count").value("1"))
                .andDo(print());

        mockMvc.perform(post("/posts/{postId}/good_count?key={userKey}", post.getId(), user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())//200
                .andExpect(jsonPath("$.good_count").value("2"))
                .andDo(print());
    }

    @Test
    @DisplayName("/posts/id/good post bad 카운터가 증가한다.")
    void test20() throws Exception {

        //given
        Post post = postRepository.save(Post.builder()
                .title("안녕하세요")
                .content("test 입니다.")
                .userKey("1234")
                .build()
        );

        User user = User.builder()
                .email("asdf")
                .userKey("1234")
                .build();
        userRepository.save(user);

        //expected
        mockMvc.perform(post("/posts/{postId}/bad_count?key={userKey}", post.getId(), user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())//200
                .andExpect(jsonPath("$.bad_count").value("1"))
                .andDo(print());

        mockMvc.perform(post("/posts/{postId}/bad_count?key={userKey}", post.getId(), user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())//200
                .andExpect(jsonPath("$.bad_count").value("2"))
                .andDo(print());

        mockMvc.perform(get("/posts?page=1&size=10&key={userKey}", user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(1)))
                .andExpect(jsonPath("$[0].title").value("안녕하세요"))
                .andExpect(jsonPath("$[0].content").value("test 입니다."))
                .andDo(print());

    }
}