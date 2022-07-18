package com.crudtest.request;

import com.crudtest.exception.InvalidRequest;
import com.querydsl.core.util.StringUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class EmailCreate {

    private String email;

    @Builder
    public EmailCreate(String email){
        this.email = email;
    }

    public void validate(){
        if(StringUtils.isNullOrEmpty(email)){
            throw new InvalidRequest("email","이메일을 입력해주세요.");
        }
    }
}
