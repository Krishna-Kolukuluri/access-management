package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.ApiResponseDto;
import com.vmware.accessmanagement.dto.GroupDto;
import com.vmware.accessmanagement.dto.GroupUserDto;

import java.util.List;

public interface GroupService {
    ApiResponseDto createGroup(GroupDto groupDto);
    GroupUserDto getGroupWithUsers(String groupName);
    List<GroupDto> getGroups();
    ApiResponseDto updateGroup(GroupUserDto groupDto);
    ApiResponseDto deleteGroup(String groupName);
}
