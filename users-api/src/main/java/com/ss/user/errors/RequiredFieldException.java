package com.ss.user.errors;
public class RequiredFieldException extends Exception{
    public RequiredFieldException (String message){
        super (message);
    }
}
