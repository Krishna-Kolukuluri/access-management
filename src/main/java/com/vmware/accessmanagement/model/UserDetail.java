package com.vmware.accessmanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vmware.accessmanagement.dto.GroupDto;
import com.vmware.accessmanagement.dto.UserDto;
import com.vmware.accessmanagement.encryption.AttributeEncryptor;
import com.vmware.accessmanagement.validator.ValidPassword;
import com.vmware.accessmanagement.validator.ValidUserName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name ="UserDetail")
@NoArgsConstructor
@Getter
@Setter
public class UserDetail {
    @Id
    @Column(name ="user_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long userId;

    @Column(name = "user_name", unique = true)
    @NotBlank(message = "UserName is mandatory")
    @ValidUserName
    //@Unique(service = UserService.class, fieldName = "userName", message = "UserName.unique.violation")
    String userName;

    @Column(name = "first_name")
    @NotBlank(message = "First Name is mandatory")
    String firstName;

    @Column(name = "last_name")
    @NotBlank(message = "Last Name is mandatory")
    String lastName;

    @Column(name = "dob")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    Date dob;

    @Column(name = "user_role")
    String userRole;

    @Column(name = "address")
    String address;

    @Convert(converter = AttributeEncryptor.class)
    @NotBlank(message = "Password is mandatory")
    @ValidPassword
    @Column(name = "password")
    String password;

    @OneToMany(mappedBy = "userDetail")
    List<UserGroup> groups;

    public UserDetail(UserDetail user) {
        if(user.getUserRole().equals(GroupRole.ADMIN.toString())){
            this.userRole = GroupRole.ADMIN.toString();
        }else{
            this.userRole = GroupRole.NON_ADMIN.toString();
        }
        this.userId = user.getUserId();
        this.firstName=user.getFirstName();
        this.lastName=user.getLastName();
        this.userName=user.getUserName();
        this.dob=user.getDob();
        this.userRole=user.getUserRole();
        this.address=user.getAddress();
        this.password=user.getPassword();
        this.groups = user.getGroups();
    }
}
