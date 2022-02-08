package com.vmware.accessmanagement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponseDto {
    private HttpStatus httpStatus;
    private String message;
    private boolean status;

    public ApiResponseDto(HttpStatus httpStatus, String message, boolean status){
        this.httpStatus = httpStatus;
        this.message = message;
        this.status = status;
    }

    @Override
    public String toString() {
        return "ApiResponseDto{" +
                "message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
