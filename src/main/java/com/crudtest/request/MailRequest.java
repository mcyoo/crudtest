package com.crudtest.request;

import com.crudtest.exception.InvalidRequest;
import com.querydsl.core.util.StringUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class MailRequest {

    private String title = "[CRUD TEST] 인증키 입니다.";
    private String email;
    private String key;

    private String content;

    @Builder
    public MailRequest(String email,String key){
        this.email = email;
        this.key = key;
        this.content = "인증키 : " + key + " 입니다.";
    }
}
