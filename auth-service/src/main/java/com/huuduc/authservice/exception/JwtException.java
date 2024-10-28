package com.huuduc.authservice.exception;

public class JwtException extends RuntimeException {

    public JwtException(String msg){
        super(msg);
    }
}
