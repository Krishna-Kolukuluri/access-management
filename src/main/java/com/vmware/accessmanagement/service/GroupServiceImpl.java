package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.*;
import com.vmware.accessmanagement.model.*;
import com.vmware.accessmanagement.repository.GroupRepository;
import com.vmware.accessmanagement.repository.UserGroupRepository;
import com.vmware.accessmanagement.repository.UserRepository;
import lombok.extern.log4j.Log4j2;

import javax.transaction.Transactional;
import javax.validation.*;

import org.modelmapper.ModelMapper;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
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
    private UserRepository userRepository;

    @Autowired
    private Validator validator;

    @Override
    public ApiResponseDto createGroup(GroupDto groupDto) {
        GroupDetail groupDetail = groupRepository.save(modelMapper.map(groupDto, GroupDetail.class));
        ApiResponseDto customMessageDto = new ApiResponseDto();
        if(Objects.nonNull(groupDetail.getId())){
            customMessageDto.setHttpStatus(HttpStatus.CREATED);
            customMessageDto.setMessage("Created Group with GroupName: '" + groupDto.getGroupName() +"'");
            customMessageDto.setStatus(true);
        }else{
            customMessageDto.setHttpStatus(HttpStatus.CONFLICT);
            customMessageDto.setMessage("Group creation failed with GroupName: '" + groupDto.getGroupName() +"'");
            customMessageDto.setStatus(false);
        }
        return customMessageDto;
    }

    @Override
    public GroupUserDto getGroupWithUsers(String groupName) {
        GroupDetail groupDetail= groupRepository.findGroupDetailByGroupName(groupName);
        if(Objects.isNull(groupDetail)){
            return new GroupUserDto();
        }
        return new GroupUserDto(groupDetail);
    }

    @Override
    public List<GroupDto> getGroups() {
        List<GroupDetail> groups = groupRepository.findAll();
        return groups.stream().map(group -> new GroupDto(group)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ApiResponseDto updateGroup(GroupUserDto groupDto) {
        GroupDetail groupDetail = groupRepository.findGroupDetailByGroupName(groupDto.getGroupName());
        if(groupDetail == null){
            throw new OpenApiResourceNotFoundException("Group not found ::" +  groupDto.getGroupName());
        }
        if(!groupDto.getGroupPermission().equals(groupDetail.getGroupPermission())){
            Set<ConstraintViolation<String>> violations = validator.validateValue(String.class,
                    groupDto.getGroupPermission(), groupDetail.getGroupPermission());
            throw new ConstraintViolationException("Changing Group permissions not allowed. original: " +
                                                    groupDetail.getGroupPermission() + " to updating: " +
                    groupDto.getGroupPermission(), violations);
        }
        groupDetail = updateUsers(groupDetail, groupDto.getUsers());
        if(Objects.isNull(groupDetail)){
            return new ApiResponseDto(HttpStatus.NOT_FOUND,"Group update failed as user(s) not found, GroupName: '" +
                    groupDto.getGroupName() +"'",  false);
        }
        groupDetail.setGroupDescription(groupDto.getGroupDescription());
        groupDetail.setGroupRole(groupDto.getGroupRole());
        groupRepository.save(groupDetail);
        return new ApiResponseDto(HttpStatus.OK,"Updated Group: '" + groupDto.getGroupName() +"'",  true);
    }

    private GroupDetail updateUsers(GroupDetail groupDetail, List<UserInGroupDto> userInGroupDtos){
        UserGroup userGroup;
        List<UserGroup> existingUsers = groupDetail.getUsers();
        for(UserInGroupDto userInGroupDto: filterUsers(existingUsers, userInGroupDtos)){
            userGroup = new UserGroup();
            UserDetail userDetail = userRepository.findUserByUserName(userInGroupDto.getUserName());
            if(Objects.nonNull(userDetail)){
                userGroup.setUserDetail(userDetail);
                userGroup.setGroupDetail(groupDetail);
                userGroupRepository.save(userGroup);
                groupDetail.getUsers().add(userGroup);
            }else{
                return null;
            }
        }
        return groupDetail;
    }

    private List<UserInGroupDto> filterUsers(List<UserGroup> existingUsers, List<UserInGroupDto>  userInGroupDtos){
        List<UserInGroupDto> filteredUsers = new ArrayList<>();
        if(Objects.nonNull(userInGroupDtos)){
            HashMap<String, UserGroup> existingUsersGroup = new HashMap<>();
            for(UserGroup userGroup: existingUsers){
                existingUsersGroup.put(userGroup.getUserDetail().getUserName(), userGroup);
            }
            if(existingUsers.size() ==0){
                filteredUsers.addAll(userInGroupDtos);
            }else{
                for(UserInGroupDto userInGroupDto: userInGroupDtos){
                    if(!existingUsersGroup.containsKey(userInGroupDto.getUserName())){
                        filteredUsers.add(userInGroupDto);
                    }
                }
            }
        }
        return filteredUsers;
    }

    @Transactional
    @Override
    public ApiResponseDto deleteGroup(String groupName) {
        ApiResponseDto customMessageDto = new ApiResponseDto();
        if(groupName.equals(ADMIN_ALL)){
            customMessageDto = new ApiResponseDto(HttpStatus.FORBIDDEN, "ADMIN_ALL default group can't be deleted.", false );
            return customMessageDto;
        }
        GroupDetail group = groupRepository.findGroupDetailByGroupName(groupName);
        if(Objects.nonNull(group)){
            userGroupRepository.deleteByGroupID(group.getId());
        }
        int count = groupRepository.deleteByGroupName(groupName);
        if(count >= 1){
            log.info(groupName +  " Group found and deleted, number of rows deleted:" + count);
            customMessageDto = new ApiResponseDto(HttpStatus.OK, groupName +  " Group found and deleted", true);
        }else if(count == 0){
            log.info(groupName + " Group not found to delete, number of rows deleted:" + count);
            customMessageDto = new ApiResponseDto(HttpStatus.NOT_FOUND, groupName + " Group not found to delete", false);
        }
        return customMessageDto;
    }
}
