package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.CustomMessageDto;
import com.vmware.accessmanagement.dto.GroupDto;
import com.vmware.accessmanagement.dto.GroupUserDto;

import java.util.List;

public interface GroupService {
    GroupDto createGroup(GroupDto groupDto);
    GroupUserDto getGroupWithUsers(String groupName);
    List<GroupDto> getGroups();
    GroupUserDto updateGroup(GroupUserDto groupDto);
    CustomMessageDto deleteGroup(String groupName);
}
