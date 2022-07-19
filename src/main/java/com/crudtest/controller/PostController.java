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
    public Map post(@RequestBody(required = false) PostCreate request, @RequestParam(required = false) String key) {
        userService.keyValid(key);
        request.validate();

        Long postId = postService.write(request,key);
        return Map.of("postId",postId);
    }

    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable Long postId,@RequestParam(required = false) String key){
        userService.keyValid(key);
        return postService.get(postId);
    }

    @GetMapping("/posts")
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch,@RequestParam(required = false) String key){
        userService.keyValid(key);
        return postService.getList(postSearch);
    }

    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable Long postId, @RequestBody PostEdit postEdit,@RequestParam(required = false) String key) {
        userService.keyValid(key);
        postEdit.validate();
        postService.edit(postId,postEdit);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId,@RequestParam(required = false) String key) {
        userService.keyValid(key);
        postService.delete(postId);
    }
}
