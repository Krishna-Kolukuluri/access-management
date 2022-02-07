package com.vmware.accessmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vmware.accessmanagement.model.UserDetail;
import com.vmware.accessmanagement.model.UserGroup;
import com.vmware.accessmanagement.validator.ValidUserName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
public class UserViewDto {
    @NotBlank(message = "firstName is mandatory")
    String firstName;
    @NotBlank(message = "lastName is mandatory")
    String lastName;
    @NotBlank(message = "UserName is mandatory")
    @ValidUserName
    String userName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    Date dob;
    String userRole;
    String address;
    List<GroupDto> groups;

    public UserViewDto(){

    }

    public UserViewDto(UserDetail user){
        //this.id = user.getUserId();
        this.firstName=user.getFirstName();
        this.lastName=user.getLastName();
        this.userName=user.getUserName();
        this.dob=user.getDob();
        this.userRole=user.getUserRole();
        this.address=user.getAddress();
        List<GroupDto> groupDtos = new ArrayList<>();
        if(user.getGroups() != null){
            for(UserGroup group : user.getGroups()){
                if(group != null) {
                    GroupDto groupDto = new GroupDto();
                    groupDto.setGroupDescription(group.getGroupDetail().getGroupDescription());
                    groupDto.setGroupName(group.getGroupDetail().getGroupName());
                    groupDto.setGroupRole(group.getGroupDetail().getGroupRole());
                    groupDto.setGroupPermission(group.getGroupDetail().getGroupPermission());
                    groupDtos.add(groupDto);
                }
            }
        }
        this.groups = groupDtos;
    }
}
