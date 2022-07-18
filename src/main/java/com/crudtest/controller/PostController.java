package com.crudtest.controller;

import com.crudtest.request.PostCreate;
import com.crudtest.request.PostEdit;
import com.crudtest.request.PostSearch;
import com.crudtest.response.PostResponse;
import com.crudtest.service.EmailService;
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

    private final EmailService emailService;

    @PostMapping("/posts")
    public Map post(@RequestBody PostCreate request,@RequestParam String key) {
        request.validate();
        emailService.keyValid(key);

        Long postId = postService.write(request,key);
        return Map.of("postId",postId);
    }

    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable Long postId,@RequestParam String key){
        emailService.keyValid(key);
        return postService.get(postId);
    }

    @GetMapping("/posts")
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch,@RequestParam String key){
        emailService.keyValid(key);
        return postService.getList(postSearch);
    }

    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable Long postId, @RequestBody PostEdit postEdit,@RequestParam String key) {
        postEdit.validate();
        emailService.keyValid(key);
        postService.edit(postId,postEdit);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId,@RequestParam String key) {
        emailService.keyValid(key);
        postService.delete(postId);
    }
}
