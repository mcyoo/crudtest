package com.crudtest.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String key;

    @Lob
    private String content;

    @Builder
    public Post(String title,String content,String key){
        this.title = title;
        this.content = content;
        this.key = key;
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

    public String getTitle(){
        //서비스의 정책을 넣지마세요!! 절대!!
        return this.title;//.substring(0,10);
    }
}
