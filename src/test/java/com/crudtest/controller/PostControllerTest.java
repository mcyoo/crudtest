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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                //.andExpect(content().string("{}"))
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
    }

    @Test
    @DisplayName("글 1개 조회")
    void test4() throws Exception {
        //given
        Post post = Post.builder()
                .title("1234567890123")
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

        /*
        Post post1 = postRepository.save(Post.builder()
                .title("1")
                .content("1")
                .build()
        );
        Post post2 = postRepository.save(Post.builder()
                .title("2")
                .content("2")
                .build()
        );
         */

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
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 작성시 제목에 '바보'는 포함될 수 없다.")
    void test10() throws Exception {
        //글 제목
        //글 내용
        PostCreate request = PostCreate.builder()
                .title("바보 제석")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        User user = User.builder()
                .email("asdf")
                .userKey("1234")
                .build();

        userRepository.save(user);

        //when
        mockMvc.perform(post("/posts?key={userKey}", user.getUserKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        //then

    }

    @Test
    @DisplayName("게시글 수정시 제목은 필수 이다.")
    void test11() throws Exception {
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
                .andDo(print());

    }

    @Test
    @DisplayName("게시글 작성시 내용은 필수이다.")
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
                .andDo(print());
    }
}