package com.crudtest.request;

import com.crudtest.exception.InvalidRequest;
import com.querydsl.core.util.StringUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ToString
@Getter
@Setter
public class PostEdit {

    @NotBlank(message = "타이틀을 입력해주세요.")
    private String title;

    @NotBlank(message = "콘텐츠를 입력해주세요.")
    private String content;

    @Builder
    public PostEdit(String title, String content){
        this.title = title;
        this.content = content;
    }

    public void validate(){
        if(StringUtils.isNullOrEmpty(title)){
            throw new InvalidRequest("title","제목은 필수 입니다.");
        }
        if(StringUtils.isNullOrEmpty(content)){
            throw new InvalidRequest("content","내용은 필수 입니다.");
        }
        if(title.length() > 20 ){
            throw new InvalidRequest("title","제목이 20자를 초과할 수 없습니다.");
        }
    }
}
