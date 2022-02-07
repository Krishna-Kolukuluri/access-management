package com.vmware.accessmanagement.dto;

import com.vmware.accessmanagement.model.GroupDetail;
import com.vmware.accessmanagement.model.UserGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GroupUserDto extends GroupDto {
    List<UserViewDto> userGroups;

    public GroupUserDto(GroupDetail groupDetail){
        //this.id = groupDetail.getId();
        this.groupName=groupDetail.getGroupName();
        this.groupDescription=groupDetail.getGroupDescription();
        this.groupRole=groupDetail.getGroupRole();
        this.groupPermission = groupDetail.getGroupPermission();
        List<UserViewDto> userDtos = new ArrayList<>();
        if(groupDetail.getUsers() != null){
            for(UserGroup userGroup : groupDetail.getUsers()){
                if(userGroup != null) {
                    UserDto userDto = new UserDto();
                    userDto.setUserName(userGroup.getUserDetail().getUserName());
                    userDto.setAddress(userGroup.getUserDetail().getAddress());
                    userDto.setDob(userGroup.getUserDetail().getDob());
                    userDto.setUserRole(userGroup.getUserDetail().getUserRole());
                    userDto.setFirstName(userGroup.getUserDetail().getFirstName());
                    userDto.setLastName(userGroup.getUserDetail().getLastName());
                    userDtos.add(userDto);
                }
            }
        }
        this.userGroups = userDtos;
    }
}
