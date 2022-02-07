package com.vmware.accessmanagement.dto;

import com.vmware.accessmanagement.model.UserDetail;
import com.vmware.accessmanagement.validator.ValidPassword;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
@NoArgsConstructor
public class UserDto extends UserViewDto {
    //private Long id;
    @NotBlank(message = "Password is mandatory")
    @ValidPassword
    String password;
    public UserDto(UserDetail user){
        super(user);
        //this.id = user.getUserId();
        this.password=user.getPassword();
    }
}
