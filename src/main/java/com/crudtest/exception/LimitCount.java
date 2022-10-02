package com.crudtest.exception;

public class LimitCount extends CrudTestException {

    private static final String MESSAGE = "최대 개수 입니다.";
    public LimitCount() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode(){
        return 404;
    }
}
