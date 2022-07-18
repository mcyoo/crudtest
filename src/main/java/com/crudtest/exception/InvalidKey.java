package com.crudtest.exception;

import lombok.Getter;

@Getter
public class InvalidKey extends PodoclubException{

    private static final String MESSAGE = "유효하지 않은 Key 값 입니다.";

    public InvalidKey() { super(MESSAGE);}

    public InvalidKey(String fieldName,String message){
        super(MESSAGE);
        addValidation(fieldName,message);
    }
    @Override
    public int getStatusCode(){
        return 400;
    }

}
