package com.crudtest.request;

import com.crudtest.exception.InvalidRequest;
import com.querydsl.core.util.StringUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ToString
@Getter
@Setter
public class UserCreate {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @Builder
    public UserCreate(String email){
        this.email = email;
    }

    public UserCreate(){
    }

    public void validate(){
        if(StringUtils.isNullOrEmpty(email)){
            throw new InvalidRequest("email","이메일을 입력해주세요.");
        }
        if(!isValidEmail(email)){
            throw new InvalidRequest("email","잘못된 이메일 형식입니다.");
        }
    }
    /**
     * Comment  : 정상적인 이메일 인지 검증.
     */
    public boolean isValidEmail(String email) {
        boolean err = false;
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(m.matches()) {
            err = true;
        }
        return err;
    }
}
