package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.CustomMessageDto;
import com.vmware.accessmanagement.dto.GroupDto;
import com.vmware.accessmanagement.dto.UserViewDto;
import com.vmware.accessmanagement.model.GroupDetail;
import com.vmware.accessmanagement.model.UserDetail;
import com.vmware.accessmanagement.model.UserGroup;
import com.vmware.accessmanagement.repository.GroupRepository;
import com.vmware.accessmanagement.repository.UserGroupRepository;
import com.vmware.accessmanagement.repository.UserRepository;
import com.vmware.accessmanagement.dto.UserDto;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Assert;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    UserGroupRepository userGroupRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserViewDto getUserWithGroups(String userName) {
        UserDetail userDetail = userRepository.findUserByUserName(userName);
        return new UserViewDto(userDetail);
    }

    @Transactional
    @Override
    public UserViewDto createUser(UserDto userDto) {
        UserDetail inputUserDetail = modelMapper.map(userDto, UserDetail.class);
        inputUserDetail.setGroups(new ArrayList<UserGroup>());
        UserDetail userDetail = userRepository.save(updateGroups(inputUserDetail, userDto.getGroups()));
        return new UserViewDto(userDetail);
    }

    @Transactional
    @Override
    public UserViewDto updateUser(UserDto userDto) {
        UserDetail userDetail = userRepository.findUserByUserName(userDto.getUserName());
        if(userDetail == null){
            throw new OpenApiResourceNotFoundException("User not found ::" +  userDto.getUserName());
        }
        userDetail = updateGroups(userDetail, userDto.getGroups());
        userDetail.setUserRole(userDto.getUserRole());
        userDetail.setAddress(userDto.getAddress());
        userDetail.setDob(userDto.getDob());
        userDetail.setFirstName(userDto.getFirstName());
        userDetail.setLastName(userDto.getLastName());
        userDetail.setPassword(userDto.getPassword());
        userRepository.save(userDetail);
        UserViewDto updateUser = new UserViewDto(userDetail);

        return updateUser;
    }

    private UserDetail updateGroups(UserDetail userDetail, List<GroupDto> groupDtos){
        UserGroup userGroup;
        List<UserGroup> existingGroups =userDetail.getGroups();
        for(GroupDto groupDto: filterGroups(existingGroups, groupDtos)){
            userGroup = new UserGroup();
            userGroup.setGroupDetail(groupRepository.findGroupDetailByGroupName(groupDto.getGroupName()));
            userGroup.setUserDetail(userDetail);
            userGroupRepository.save(userGroup);
            userDetail.getGroups().add(userGroup);
        }
        return userDetail;
    }

    private List<GroupDto> filterGroups(List<UserGroup> existingGroups, List<GroupDto> groupDtos){
        List<GroupDto> filterGroups = new ArrayList<>();
        if(groupDtos != null){
            HashMap<String, UserGroup> existingGroupsHash = new HashMap<>();
            for(UserGroup userGroup: existingGroups){
                existingGroupsHash.put(userGroup.getGroupDetail().getGroupName(), userGroup);
            }
            if(existingGroups.size() == 0){
                filterGroups.addAll(groupDtos);
                return filterGroups;
            }
            for(GroupDto groupDto: groupDtos){
                if(!existingGroupsHash.containsKey(groupDto.getGroupName())){
                    filterGroups.add(groupDto);
                }
            }
        }
        return filterGroups;
    }

    @Transactional
    @Override
    public CustomMessageDto deleteUser(String userName) {
        CustomMessageDto customMessageDto = new CustomMessageDto();
        deleteGroupRelation(userName);
        int count = userRepository.deleteByUserName(userName);
        if(count >= 1){
            customMessageDto.setMessage("User found and deleted, number of rows deleted:" + count);
            customMessageDto.setStatus(true);
        }else if(count == 0){
            customMessageDto.setMessage("User not found to delete, number of rows deleted:" + count);
            customMessageDto.setStatus(false);
        }
        return customMessageDto;
    }

    private int deleteGroupRelation(String userName){
        UserDetail userDetail = userRepository.findUserByUserName(userName);
        int count = 0;
        for(UserGroup userGroup: userDetail.getGroups()){
            userGroupRepository.deleteById(userGroup.getId());
            count++;
        }
        return count;
    }

    @Override
    public List<UserViewDto> getUsers() {
        List<UserDetail> users = userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user, UserViewDto.class)).collect(Collectors.toList());
    }

    @Override
    public boolean fieldValueExists(Object value, String fieldName) throws UnsupportedOperationException {
        Assert.notNull(fieldName);
        if (!fieldName.equals("userName")) {
            throw new UnsupportedOperationException("Field name not supported");
        }
        if (value == null) {
            return false;
        }
        String userName = value.toString();
        return  this.userRepository.existsByUserName(userName);
    }
}
