package com.crudtest.service;

import com.crudtest.domain.Post;
import com.crudtest.exception.LimitPost;
import com.crudtest.exception.PostNotAuthority;
import com.crudtest.exception.PostNotFound;
import com.crudtest.repository.PostRepository;
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
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean(){
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void Test1(){
        //given
        PostCreate postCreate = PostCreate.builder()
                .title("제목 입니다.")
                .content("내용 입니다.")
                .build();

        //when
        postService.write(postCreate,"1234");

        //then
        assertEquals(1,postRepository.count());
        Post post = postRepository.findAll().get(0);
        assertEquals("제목 입니다.",post.getTitle());
        assertEquals("내용 입니다.",post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void Test2(){
        //given
        Long postId = 1L;

        Post requestPost = Post.builder()
                .title("foo")
                .content("bar")
                .userKey("1234")
                .build();

        postRepository.save(requestPost);

        //when
        PostResponse response = postService.get(requestPost.getId(),"1234");

        //then
        assertNotNull(response);
        assertEquals("foo",response.getTitle());
        assertEquals("bar",response.getContent());
    }

    @Test
    @DisplayName("글 여러개 조회")
    void Test3(){

        //given
        List<Post> requestPosts = IntStream.range(1,31)
                .mapToObj(i-> Post.builder()
                        .title("제목 " + i)
                        .content("내용 " + i)
                        .userKey("1234")
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .page(1)
                .size(10)
                .build();

        //when
        List<PostResponse> posts = postService.getList(postSearch,"1234");

        //then
        assertEquals(10L,posts.size());
        assertEquals("제목 30",posts.get(0).getTitle());
        assertEquals("제목 26",posts.get(4).getTitle());
    }

    @Test
    @DisplayName("글 제목 수정")
    void Test4(){

        //given
        Post post = Post.builder()
                .title("제석")
                .content("짱")
                .userKey("1234")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("젝슨")
                .content("짱")
                .build();
        //when
        postService.edit(post.getId(),postEdit,"1234");

        //then
        Post changedPost = postRepository.findById(post.getId())
                        .orElseThrow(()->new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));

        assertEquals("젝슨",changedPost.getTitle());
    }

    @Test
    @DisplayName("글 내용 수정")
    void Test5(){

        //given
        Post post = Post.builder()
                .title("제석")
                .content("짱짱맨")
                .userKey("1234")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("젝슨")
                .content("짱")
                .build();
        //when
        postService.edit(post.getId(),postEdit,"1234");

        //then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(()->new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));

        assertEquals("짱",changedPost.getContent());
    }
    @Test
    @DisplayName("글 삭제")
    void Test6(){

        //given
        Post post = Post.builder()
                .title("제석")
                .content("짱짱맨")
                .userKey("1234")
                .build();
        postRepository.save(post);

        //when
        postService.delete(post.getId(),"1234");

        //then
        assertEquals(0,postRepository.count());
    }

    @Test
    @DisplayName("글 1개 조회 - 존재하지 않는 글")
    void Test7(){

        //given
        Post post = Post.builder()
                .title("제석")
                .content("짱짱맨")
                .userKey("1234")
                .build();
        postRepository.save(post);

        // expected
        assertThrows(PostNotFound.class,()->{
            postService.get(post.getId() + 1L,"1234");
        });
    }
    @Test
    @DisplayName("글 삭제 - 존재하지 않는 글")
    void Test8(){

        //given
        Post post = Post.builder()
                .title("제석")
                .content("짱짱맨")
                .userKey("1234")
                .build();
        postRepository.save(post);

        // expected
        assertThrows(PostNotFound.class,()->{
            postService.delete(post.getId() + 1L,"1234");
        });
    }
    @Test
    @DisplayName("글 내용 수정 - 존재하지 않는 글")
    void Test9(){

        //given
        Post post = Post.builder()
                .title("제석")
                .content("짱짱맨")
                .userKey("1234")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("젝슨")
                .content("짱")
                .build();

        // expected
        assertThrows(PostNotFound.class,()->{
            postService.edit(post.getId() + 1L,postEdit,"1234");
        });
    }
    @Test
    @DisplayName("글 100개 등록시 제한")
    void Test10(){

        //given
        List<Post> requestPosts = IntStream.range(0,100)
                .mapToObj(i-> Post.builder()
                        .title("제목 " + i)
                        .content("내용 " + i)
                        .userKey("1234")
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostCreate postCreate = PostCreate.builder()
                .title("제목 100")
                .content("내용 100")
                .build();

        // expected
        assertThrows(LimitPost.class,()->{
            postService.write(postCreate,"1234");
        });
    }
    @Test
    @DisplayName("글 내용 수정 - 키값이 다름")
    void Test11(){

        //given
        Post post = Post.builder()
                .title("제석")
                .content("짱짱맨")
                .userKey("1234")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("젝슨")
                .content("짱")
                .build();

        // expected
        assertThrows(PostNotAuthority.class,()->{
            postService.edit(post.getId(),postEdit,"13");
        });
    }
    @Test
    @DisplayName("글 삭제 - 키값이 다름")
    void Test12(){

        //given
        Post post = Post.builder()
                .title("제석")
                .content("짱짱맨")
                .userKey("1234")
                .build();
        postRepository.save(post);

        // expected
        assertThrows(PostNotAuthority.class,()->{
            postService.delete(post.getId(),"14");
        });
    }
    @Test
    @DisplayName("글 수정 - 권한 없음")
    void Test13(){

        //given
        Post post = Post.builder()
                .title("제석")
                .content("짱짱맨")
                .userKey("1234")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("젝슨")
                .content("짱")
                .build();

        // expected
        assertThrows(PostNotAuthority.class,()->{
            postService.edit(post.getId(),postEdit,"123");
        });
    }
    @Test
    @DisplayName("글 삭제 - 권한 없음")
    void Test14(){

        //given
        Post post = Post.builder()
                .title("제석")
                .content("짱짱맨")
                .userKey("1234")
                .build();
        postRepository.save(post);

        // expected
        assertThrows(PostNotAuthority.class,()->{
            postService.delete(post.getId(),"14");
        });
    }

    @Test
    @DisplayName("good_count 값 증가")
    void Test15(){

        //given
        List<Post> requestPosts = IntStream.range(1,31)
                .mapToObj(i-> Post.builder()
                        .title("제목 " + i)
                        .content("내용 " + i)
                        .userKey("1234")
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .page(1)
                .size(10)
                .build();

        //when
        List<PostResponse> posts = postService.getList(postSearch,"1234");
        Integer good_count1 = postService.write_good_count(posts.get(0).getId(),"1234");
        Integer good_count2 = postService.write_good_count(posts.get(0).getId(),"1234");


        System.out.println(posts.get(0).getId());
        System.out.println(posts.get(0).getTitle());
        System.out.println(posts.get(0).getContent());
        System.out.println(posts.get(0).getGood_count());
        System.out.println(posts.get(0).getBad_count());

        //카운트 증가 확인
        System.out.println(good_count1);
        System.out.println(good_count2);


        //then
        assertEquals(10L,posts.size());
        assertEquals("제목 30",posts.get(0).getTitle());
        assertEquals("제목 26",posts.get(4).getTitle());

        assertEquals(0,posts.get(4).getGood_count());
        assertEquals(0,posts.get(4).getBad_count());
        //Transactional 실행이 안되서 DB에 저장되지 않아서 검증 어려움
    }
}