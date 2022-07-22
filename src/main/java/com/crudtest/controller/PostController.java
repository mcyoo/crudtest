package com.crudtest.controller;

import com.crudtest.request.PostCreate;
import com.crudtest.request.PostEdit;
import com.crudtest.request.PostSearch;
import com.crudtest.response.PostResponse;
import com.crudtest.service.UserService;
import com.crudtest.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;

    @PostMapping("/posts")
    public Map post(@RequestBody(required = false) PostCreate request, @RequestParam(required = false) String userKey) {
        userService.userKeyValid(userKey);
        request.validate();

        Long postId = postService.write(request,userKey);
        return Map.of("postId",postId);
    }

    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable Long postId,@RequestParam(required = false) String userKey){
        userService.userKeyValid(userKey);
        return postService.get(postId,userKey);
    }

    @GetMapping("/posts")
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch,@RequestParam(required = false) String userKey){
        userService.userKeyValid(userKey);
        return postService.getList(postSearch,userKey);
    }

    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable Long postId, @RequestBody PostEdit postEdit,@RequestParam(required = false) String userKey) {
        userService.userKeyValid(userKey);
        postEdit.validate();
        postService.edit(postId,postEdit,userKey);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId,@RequestParam(required = false) String userKey) {
        userService.userKeyValid(userKey);
        postService.delete(postId,userKey);
    }
}
