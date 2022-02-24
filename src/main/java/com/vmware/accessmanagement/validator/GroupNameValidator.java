package com.vmware.accessmanagement.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupNameValidator implements ConstraintValidator<ValidGroupName, String> {
    @Override
    public void initialize(ValidGroupName constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String groupName, ConstraintValidatorContext constraintValidatorContext) {
        /*
        *
          ^[a-zA-Z0-9]      # start with an alphanumeric character
          (                 # start of (group 1)
            [._-](?![._-])  # follow by a dot, hyphen, or underscore, negative lookahead to
                            # ensures dot, hyphen, and underscore does not appear consecutively
            |               # or
            [a-zA-Z0-9]     # an alphanumeric character
          )                 # end of (group 1)
          {3,18}            # ensures the length of (group 1) between 3 and 18
          [a-zA-Z0-9]$      # end with an alphanumeric character

                            # {3,18} plus the first and last alphanumeric characters,
                            # total length became {5,20}
        * */
        String regex = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(groupName);
        return matcher.matches();
    }
}
