package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.CustomMessageDto;
import com.vmware.accessmanagement.dto.GroupDto;
import com.vmware.accessmanagement.model.GroupDetail;

import java.util.List;

public interface GroupService {
    GroupDto createGroup(GroupDto groupDto);
    GroupDto getGroupDetail(String groupName);
    List<GroupDto> getGroups();
    GroupDto updateGroup(GroupDetail groupDetail);
    CustomMessageDto deleteGroup(String groupName);
}
