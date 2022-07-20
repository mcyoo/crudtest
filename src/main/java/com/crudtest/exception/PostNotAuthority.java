package com.crudtest.exception;

import lombok.Getter;

@Getter
public class PostNotAuthority extends CrudTestException {

    private static final String MESSAGE = "권한이 없습니다.";

    public PostNotAuthority() { super(MESSAGE);}

    public PostNotAuthority(String fieldName, String message){
        super(MESSAGE);
        addValidation(fieldName,message);
    }

    @Override
    public int getStatusCode(){
        return 401;
    }

}
