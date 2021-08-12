package com.ss.user.errors;

public class ConfirmationTokenExpiredException extends Exception{
    public ConfirmationTokenExpiredException(String s){
        super(s);
    }
}
