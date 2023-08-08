package com.spring.webflux.security.app.models.reqresModel;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReqRespModelImpl<T> implements IReqRespModel<T> {

    private T data;

    private String message;

    @Override
    public T getData() {
        return this.data;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
