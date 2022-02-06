package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.GroupDto;
import com.vmware.accessmanagement.model.GroupDetail;
import com.vmware.accessmanagement.model.GroupRole;
import com.vmware.accessmanagement.model.GroupPermission;
import com.vmware.accessmanagement.repository.GroupRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.List;

@Service
@Log4j2
public class GroupServiceImpl implements GroupService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public GroupDto createGroup(GroupDto groupDto) {
        GroupDetail groupDetail = new GroupDetail();
        groupDetail.setGroupName(groupDto.getGroupName());
        groupDetail.setGroupDescription(groupDto.getGroupDescription());
        if(groupDto.getGroupPermission().equals(GroupPermission.ALL.toString())){
            groupDetail.setGroupPermission(GroupPermission.ALL.toString());
        }else if(groupDto.getGroupPermission().equals(GroupPermission.READ.toString())){
            groupDetail.setGroupPermission(GroupPermission.READ.toString());
        }else if(groupDto.getGroupPermission().equals(GroupPermission.WRITE.toString())){
            groupDetail.setGroupPermission(GroupPermission.WRITE.toString());
        }else{
            groupDetail.setGroupPermission(GroupPermission.NONE.toString());
        }
        if(groupDto.getGroupRole().equals(GroupRole.ADMIN.toString())){
            groupDetail.setGroupRole(GroupRole.ADMIN.toString());
        }else{
            groupDetail.setGroupRole(GroupRole.NON_ADMIN.toString());
        }
        return new GroupDto(groupRepository.save(groupDetail));
    }

    @Override
    public GroupDto getGroupDetail(String groupName) {
        return new GroupDto(groupRepository.findGroupDetailByGroupName(groupName));
    }

    @Override
    public List<GroupDto> getGroups() {
        return null;
    }
}
