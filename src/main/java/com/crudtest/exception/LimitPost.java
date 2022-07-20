package com.crudtest.exception;

public class LimitPost extends CrudTestException {

    private static final String MESSAGE = "100개 이상 저장할 수 없습니다.";
    public LimitPost() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode(){
        return 404;
    }
}
