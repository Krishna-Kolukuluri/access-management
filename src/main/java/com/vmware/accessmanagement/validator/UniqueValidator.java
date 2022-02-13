package com.vmware.accessmanagement.validator;

import com.vmware.accessmanagement.util.AppContext;
import com.vmware.accessmanagement.validator.FieldValueExists;
import com.vmware.accessmanagement.validator.Unique;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class UniqueValidator implements ConstraintValidator<Unique, Object> {
    @Autowired
    private ApplicationContext applicationContext;

    private FieldValueExists service;
    private String fieldName;

    @Override
    public void initialize(Unique constraintAnnotation) {
        Class<? extends FieldValueExists> clazz = constraintAnnotation.service();
        this.fieldName = constraintAnnotation.fieldName();
        String serviceQualifier = constraintAnnotation.serviceQualifier();
        applicationContext = AppContext.getInstance().getContext();
        if (!serviceQualifier.equals("")) {
            this.service = this.applicationContext.getBean(serviceQualifier, clazz);
        } else {
            this.service = this.applicationContext.getBean(clazz);
        }
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return !this.service.fieldValueExists(o, this.fieldName);
    }
}