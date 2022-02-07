package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.*;
import com.vmware.accessmanagement.model.*;
import com.vmware.accessmanagement.repository.GroupRepository;
import com.vmware.accessmanagement.repository.UserGroupRepository;
import lombok.extern.log4j.Log4j2;

import javax.transaction.Transactional;
import javax.validation.*;

import org.modelmapper.ModelMapper;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
public class GroupServiceImpl implements GroupService {
    private static final String ADMIN_ALL = "ADMIN_ALL";
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private Validator validator;

    @Override
    public GroupDto createGroup(GroupDto groupDto) {
        return new GroupDto(groupRepository.save(modelMapper.map(groupDto, GroupDetail.class)));
    }

    @Override
    public GroupUserDto getGroupWithUsers(String groupName) {
        GroupDetail groupDetail= groupRepository.findGroupDetailByGroupName(groupName);
        return new GroupUserDto(groupDetail);
    }

    @Override
    public List<GroupDto> getGroups() {
        List<GroupDetail> groups = groupRepository.findAll();
        return groups.stream().map(group -> modelMapper.map(group, GroupDto.class)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public GroupDto updateGroup(GroupDetail groupDetail) {
        GroupDetail groupDetail1 = groupRepository.findGroupDetailByGroupName(groupDetail.getGroupName());
        if(groupDetail1 == null){
            throw new OpenApiResourceNotFoundException("Group not found ::" +  groupDetail.getGroupName());
        }
        if(!groupDetail.getGroupPermission().equals(groupDetail1.getGroupPermission())){
            Set<ConstraintViolation<String>> violations = validator.validateValue(String.class,
                    groupDetail.getGroupPermission(), groupDetail1.getGroupPermission());
            throw new ConstraintViolationException("Changing Group permissions not allowed. original: " +
                                                    groupDetail1.getGroupPermission() + " to updating: " +
                                                    groupDetail.getGroupPermission(), violations);
        }
        groupDetail1.setGroupDescription(groupDetail.getGroupDescription());
        groupDetail1.setGroupRole(groupDetail.getGroupRole());
        GroupDto updateDto = new GroupDto(groupRepository.save(groupDetail1));
        return updateDto;
    }

    @Transactional
    @Override
    public CustomMessageDto deleteGroup(String groupName) {

        CustomMessageDto customMessageDto = new CustomMessageDto();
        if(groupName.equals(ADMIN_ALL)){
            customMessageDto.setMessage("ADMIN_ALL default group can't be deleted.");
            customMessageDto.setStatus(false);
            return customMessageDto;
        }
        GroupDetail group = groupRepository.findGroupDetailByGroupName(groupName);
        if(Objects.nonNull(group)){
            userGroupRepository.deleteByGroupID(group.getId());
        }
        int count = groupRepository.deleteByGroupName(groupName);
        if(count >= 1){
            customMessageDto.setMessage("Group found and deleted, number of rows deleted:" + count);
            customMessageDto.setStatus(true);
        }else if(count == 0){
            customMessageDto.setMessage("Group not found to delete, number of rows deleted:" + count);
            customMessageDto.setStatus(false);
        }
        return customMessageDto;
    }
}
