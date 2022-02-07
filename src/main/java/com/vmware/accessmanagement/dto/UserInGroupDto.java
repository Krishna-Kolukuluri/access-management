package com.vmware.accessmanagement.dto;

import com.vmware.accessmanagement.validator.ValidUserName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class UserInGroupDto {
    @NotBlank(message = "firstName is mandatory")
    String firstName;
    @NotBlank(message = "lastName is mandatory")
    String lastName;
    @NotBlank(message = "UserName is mandatory")
    @ValidUserName
    String userName;
    String userRole;
}
