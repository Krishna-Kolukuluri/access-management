package com.vmware.accessmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vmware.accessmanagement.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dob;
    private Boolean isAdmin;
    private String address;
    private String password;

    public UserDto(User user){
        this.id = user.getId();
        this.firstName=user.getFirstName();
        this.lastName=user.getLastName();
        this.userName=user.getUserName();
        this.dob=user.getDob();
        this.isAdmin=user.getIsAdmin();
        this.address=user.getAddress();
        this.password=user.getPassword();
    }
}
