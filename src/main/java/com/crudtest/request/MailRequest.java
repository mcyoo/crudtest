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
    private String userKey;

    private String content;

    @Builder
    public MailRequest(String email,String userKey){
        this.email = email;
        this.userKey = userKey;
        this.content = "인증키 " + userKey + " 입니다.";
    }
}
