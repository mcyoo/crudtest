package com.crudtest.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class PodoclubException extends RuntimeException {

    private final Map<String, String> validation = new HashMap<>();
    public PodoclubException(String message) {
        super(message);
    }

    public PodoclubException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public void addValidation(String fieldName,String message){
        validation.put(fieldName,message);
    }

}
