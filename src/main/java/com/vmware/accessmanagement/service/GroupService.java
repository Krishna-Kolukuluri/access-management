package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.ApiResponseDto;
import com.vmware.accessmanagement.dto.GroupDetailDto;
import com.vmware.accessmanagement.dto.GroupUpdateDto;
import com.vmware.accessmanagement.dto.GroupUserDto;

import java.util.List;

public interface GroupService {
    ApiResponseDto createGroup(GroupDetailDto groupDto);
    GroupUserDto getGroupWithUsers(String groupName);
    List<GroupDetailDto> getGroups();
    ApiResponseDto updateGroupDetail(String groupName, GroupUpdateDto groupDetailDto);
    ApiResponseDto addUsersToGroup(String groupName, List<String> userNames);
    ApiResponseDto deleteUsersFromGroup(String groupName, List<String> userNames);
    ApiResponseDto deleteGroup(String groupName);
}
