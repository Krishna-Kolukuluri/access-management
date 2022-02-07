package com.vmware.accessmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vmware.accessmanagement.model.GroupDetail;
import com.vmware.accessmanagement.model.GroupRole;
import com.vmware.accessmanagement.model.UserDetail;
import com.vmware.accessmanagement.model.UserGroup;
import com.vmware.accessmanagement.validator.ValidPassword;
import com.vmware.accessmanagement.validator.ValidUserName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
