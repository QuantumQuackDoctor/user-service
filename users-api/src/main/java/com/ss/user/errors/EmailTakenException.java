package com.ss.user.errors;

public class EmailTakenException extends Exception{
    public EmailTakenException(String message){
        super(message);
    }
}
