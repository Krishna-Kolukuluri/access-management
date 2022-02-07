package com.vmware.accessmanagement.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword arg0) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        /*
        *
        ^ represents starting character of the string.
        (?=.*[0-9]) represents a digit must occur at least once.
        (?=.*[a-z]) represents a lower case alphabet must occur at least once.
        (?=.*[A-Z]) represents an upper case alphabet that must occur at least once.
        (?=.*[@#$%^&-+=()] represents a special character that must occur at least once.
        (?=\\S+$) white spaces don’t allowed in the entire string.
        .{8, 20} represents at least 8 characters and at most 20 characters.
        $ represents the end of the string.
        * */
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
       return matcher.matches();
    }
}
