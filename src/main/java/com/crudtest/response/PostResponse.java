package com.crudtest.response;

import com.crudtest.domain.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponse {
    private final Long id;
    private final String title;
    private final String content;

    private final Integer good_count;
    private final Integer bad_count;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private final LocalDateTime createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private final LocalDateTime modifiedDate;

    public PostResponse(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createDate = post.getCreatedDate();
        this.modifiedDate = post.getModifiedDate();

        this.good_count = post.getGood_count();
        this.bad_count = post.getBad_count();
    }

    @Builder
    public PostResponse(Long id, String title, String content, LocalDateTime createDate, LocalDateTime modifiedDate,Integer good_count,Integer bad_count) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
        this.good_count = good_count;
        this.bad_count = bad_count;
    }
}
