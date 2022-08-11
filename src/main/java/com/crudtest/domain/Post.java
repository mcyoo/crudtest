package com.crudtest.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String userKey;

    @Lob
    private String content;

    @Builder
    public Post(String title,String content,String userKey){
        this.title = title;
        this.content = content;
        this.userKey = userKey;
    }

    public PostEditor.PostEditorBuilder toEditor() {
        return PostEditor.builder()
                .title(title)
                .content(content);
    }
    public void edit(PostEditor postEditor){
        title = postEditor.getTitle();
        content = postEditor.getContent();
    }
}
