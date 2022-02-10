package com.vmware.accessmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vmware.accessmanagement.validator.ValidPassword;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailDto extends UserInGroupDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    Date dob;
    String address;
    @ValidPassword
    String password;
}
