package com.crudtest.service;

import com.crudtest.domain.Post;
import com.crudtest.domain.PostEditor;
import com.crudtest.exception.LimitCount;
import com.crudtest.exception.LimitPost;
import com.crudtest.exception.PostNotAuthority;
import com.crudtest.exception.PostNotFound;
import com.crudtest.repository.PostRepository;
import com.crudtest.request.PostCreate;
import com.crudtest.request.PostEdit;
import com.crudtest.request.PostSearch;
import com.crudtest.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Long write(PostCreate postCreate,String userKey){
        List<Post> posts = postRepository.findAllByuserKey(userKey);
        if(posts.size() == 100) {
            throw new LimitPost();
        }
        //postCreate => Entity
        Post post = Post.builder()
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .userKey(userKey)
                .build();

        postRepository.save(post);

        return post.getId();
    }

    public PostResponse get(Long id,String userKey){
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        if(!post.getUserKey().equals(userKey)) {
            throw new PostNotAuthority("id","확인할 권한이 없습니다.");
        }

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createDate(post.getCreatedDate())
                .modifiedDate(post.getModifiedDate())
                .good_count(post.getGood_count())
                .bad_count(post.getBad_count())
                .build();
    }

    @Transactional
    public Integer write_good_count(Long id,String userKey){
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        if(!post.getUserKey().equals(userKey)) {
            throw new PostNotAuthority("id","확인할 권한이 없습니다.");
        }

        if(post.getGood_count() == 10000) {
            throw new LimitCount();
        }

        post.setGood_count(post.getGood_count()+1);

        return post.getGood_count();
    }

    @Transactional
    public Integer write_bad_count(Long id, String userKey){
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        if(!post.getUserKey().equals(userKey)) {
            throw new PostNotAuthority("id","확인할 권한이 없습니다.");
        }

        if(post.getBad_count() == 10000) {
            throw new LimitCount();
        }

        post.setBad_count(post.getBad_count()+1);

        return post.getBad_count();
    }

    //글이 너무 많은 경우 -> 비용이 너무 많이 든다.
    //글이 100,000,000 개인 경우 -> DB가 뻗을 수 있다.
    //DB -> 애플리케이션 서버로 전달되는 시간 ,트래픽 비용 발생
    public List<PostResponse> getList(PostSearch postSearch,String userKey){
        return postRepository.getList(postSearch,userKey).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void edit(Long id, PostEdit postEdit,String userKey){
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        if(!post.getUserKey().equals(userKey)) {
            throw new PostNotAuthority("id","수정할 권한이 없습니다.");
        }
        PostEditor.PostEditorBuilder editorBuilder = post.toEditor();

        PostEditor postEditor = editorBuilder.title(postEdit.getTitle())
                        .content(postEdit.getContent())
                                .build();

        post.edit(postEditor);
    }

    public void delete(Long id,String userKey) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        if(!post.getUserKey().equals(userKey)) {
            throw new PostNotAuthority("id","삭제할 권한이 없습니다.");
        }

        postRepository.delete(post);
    }
}
