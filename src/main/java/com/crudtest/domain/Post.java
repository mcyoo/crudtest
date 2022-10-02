package com.crudtest.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.NotNull;


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

    private Integer good_count = 0;

    private Integer bad_count = 0;

    @Builder
    public Post(String title, String content, String userKey){
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

    public void setGood_count(Integer good_count){
        this.good_count = good_count;
    }

    public void setBad_count(Integer bad_count){
        this.bad_count = bad_count;
    }
}
