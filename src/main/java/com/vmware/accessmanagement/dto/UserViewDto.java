package com.vmware.accessmanagement.dto;

import com.vmware.accessmanagement.model.UserDetail;
import com.vmware.accessmanagement.model.UserGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class UserViewDto {
    String firstName;
    String lastName;
    String userName;
    Date dob;
    String userRole;
    String address;
    List<GroupDetailDto> groups;

    public UserViewDto(UserDetail user){
        //this.id = user.getUserId();
        this.firstName=user.getFirstName();
        this.lastName=user.getLastName();
        this.userName=user.getUserName();
        this.dob=user.getDob();
        this.userRole=user.getUserRole();
        this.address=user.getAddress();
        List<GroupDetailDto> groupDtos = new ArrayList<>();
        if(user.getGroups() != null){
            for(UserGroup group : user.getGroups()){
                if(group != null) {
                    GroupDetailDto groupDto = new GroupDetailDto();
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
