package com.vmware.accessmanagement.dto;

import com.vmware.accessmanagement.model.GroupDetail;
import com.vmware.accessmanagement.model.UserGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class GroupUserDto extends GroupDetailDto {
    List<UserInGroupDto> users;

    public GroupUserDto(GroupDetail groupDetail){
        //this.id = groupDetail.getId();
        this.groupName=groupDetail.getGroupName();
        this.groupDescription=groupDetail.getGroupDescription();
        this.groupRole=groupDetail.getGroupRole();
        this.groupPermission = groupDetail.getGroupPermission();
        List<UserInGroupDto>  userDtos = new ArrayList<>();
        if(Objects.nonNull(groupDetail.getUsers())){
            for(UserGroup userGroup: groupDetail.getUsers()){
                UserInGroupDto userDto = new UserInGroupDto();
                userDto.setUserName(userGroup.getUserDetail().getUserName());
                userDto.setFirstName(userGroup.getUserDetail().getFirstName());
                userDto.setLastName(userGroup.getUserDetail().getLastName());
                userDto.setUserRole(userGroup.getUserDetail().getUserRole());
                userDtos.add(userDto);
            }
        }
        this.users = userDtos;
    }
}
