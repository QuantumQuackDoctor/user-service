package com.ss.user.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        specialCharPattern = Pattern.compile("[^a-zA-Z0-9]");
        whiteSpacePattern = Pattern.compile("\\s");
        numberPattern = Pattern.compile("[0-9]");
        capitalPattern = Pattern.compile("[A-Z]");
    }

    Pattern whiteSpacePattern;
    Pattern specialCharPattern;
    Pattern numberPattern;
    Pattern capitalPattern;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null) return false;
        //no white space
        if (whiteSpacePattern.matcher(s).find()) return false;
        //min length 8
        if (s.length() < 8) return false;
        //one special character
        if (!specialCharPattern.matcher(s).find()) return false;
        //one number
        if (!numberPattern.matcher(s).find()) return false;
        //uppercase
        return capitalPattern.matcher(s).find();
    }
}
