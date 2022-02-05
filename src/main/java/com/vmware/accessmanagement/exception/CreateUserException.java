package com.vmware.accessmanagement.exception;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason="Error while creating user")
@ComponentScan
public class CreateUserException extends RuntimeException{
}
