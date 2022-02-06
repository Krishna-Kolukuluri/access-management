package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.GroupDto;
import com.vmware.accessmanagement.dto.UserDto;
import com.vmware.accessmanagement.model.GroupDetail;
import com.vmware.accessmanagement.model.GroupRole;
import com.vmware.accessmanagement.model.GroupPermission;
import com.vmware.accessmanagement.model.UserDetail;
import com.vmware.accessmanagement.repository.GroupRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class GroupServiceImpl implements GroupService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public GroupDto createGroup(GroupDto groupDto) {
        return new GroupDto(groupRepository.save(modelMapper.map(groupDto, GroupDetail.class)));
    }

    @Override
    public GroupDto getGroupDetail(String groupName) {
        return new GroupDto(groupRepository.findGroupDetailByGroupName(groupName));
    }

    @Override
    public List<GroupDto> getGroups() {
        List<GroupDetail> groups = groupRepository.findAll();
        return groups.stream().map(group -> modelMapper.map(group, GroupDto.class)).collect(Collectors.toList());
    }
}
