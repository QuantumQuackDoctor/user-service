package com.ss.user.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        //no white space
        if (s.matches("\\s")) return false;
        //min length 8
        if (s.length() >= 8) return false;
        //one special character
        if (!s.matches("[^a-zA-Z0-9\\s]")) return false;
        //one number
        if (!s.matches("[0-9]")) return false;
        //uppercase
        return s.matches("[A-Z]");
    }
}
