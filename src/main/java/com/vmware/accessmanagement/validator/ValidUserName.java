package com.vmware.accessmanagement.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = UserNameValidator.class)
@Target({ElementType.PARAMETER, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface ValidUserName {
    String message() default "Invalid User Name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
