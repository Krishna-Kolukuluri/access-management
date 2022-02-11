package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.*;
import com.vmware.accessmanagement.model.GroupDetail;
import com.vmware.accessmanagement.model.GroupRole;
import com.vmware.accessmanagement.model.UserDetail;
import com.vmware.accessmanagement.model.UserGroup;
import com.vmware.accessmanagement.repository.GroupRepository;
import com.vmware.accessmanagement.repository.UserGroupRepository;
import com.vmware.accessmanagement.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Assert;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * UserService to create, delete, update users and add, delete groups for a user.
 */
@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private static final String ADMIN_ALL ="ADMIN_ALL";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserViewDto getUserWithGroups(String userName) {
        UserDetail userDetail = userRepository.findUserByUserName(userName);
        if(Objects.isNull(userDetail)){
            return new UserViewDto();
        }
        return new UserViewDto(userDetail);
    }

    @Transactional
    @Override
    public ApiResponseDto createUser(UserDetailDto userDto) {
        UserDetail inputUserDetail = modelMapper.map(userDto, UserDetail.class);
        inputUserDetail.setGroups(new ArrayList<UserGroup>());
        List<GroupDetailDto> groupDtos = new ArrayList<>();
        if(inputUserDetail.getUserRole().equals(GroupRole.ADMIN.toString())){
            GroupDetailDto groupDto = new GroupDetailDto();
            groupDto.setGroupName(ADMIN_ALL);
            groupDtos.add(groupDto);
        }
        UserDetail userDetail = updateGroups(inputUserDetail, groupDtos);
        if(Objects.isNull(userDetail)){
           return new ApiResponseDto(HttpStatus.CONFLICT,"User:" + userDto.getUserName()  +
                   " creation failed due to missing groups.", false);
        }
        userDetail = userRepository.save(userDetail);
        ApiResponseDto customMessageDto = new ApiResponseDto();
        if(Objects.nonNull(userDetail.getUserId())){
            customMessageDto = new ApiResponseDto(HttpStatus.CREATED,"Created User with UserName: '" +
                    userDetail.getUserName() +"'", true);
        }else{
            customMessageDto = new ApiResponseDto(HttpStatus.CONFLICT,"User creation failed with UserName: '" +
                    userDetail.getUserName() +"'", false);
        }
        return customMessageDto;
    }

    @Transactional
    @Override
    public UserViewDto updateUser(String userName, UserUpdateDto userDto) {
        UserDetail userDetail = getUserDetails(userName);
        List<UserGroup> deleteGroups = new ArrayList<>();
        List<GroupDetailDto> addGroups = new ArrayList<>();
        if(!userDetail.getUserRole().equals(userDto.getUserRole())){
            GroupDetailDto groupDto = new GroupDetailDto();
            if(userDto.getUserRole().equals(GroupRole.ADMIN.toString())){
                groupDto.setGroupName(ADMIN_ALL);
                addGroups.add(groupDto);
            }else{
                List<UserGroup> userGroups = new ArrayList<UserGroup>(userDetail.getGroups());
                for(UserGroup userGroup: userGroups){
                    if(userGroup.getGroupDetail().getGroupRole().equals(GroupRole.ADMIN.toString())){
                        deleteGroups.add(userGroup);
                        userDetail.getGroups().remove(userGroup);
                    }
                }
            }
        }
        deleteGroupRelation(deleteGroups);
        userDetail = updateGroups(userDetail, addGroups);
        if(Objects.nonNull(userDto.getUserRole())){
            userDetail.setUserRole(userDto.getUserRole());
        }
        if(Objects.nonNull(userDto.getAddress())){
            userDetail.setAddress(userDto.getAddress());
        }
        if(Objects.nonNull(userDto.getDob())){
            userDetail.setDob(userDto.getDob());
        }
        if(Objects.nonNull(userDto.getFirstName())){
            userDetail.setFirstName(userDto.getFirstName());
        }
        if(Objects.nonNull(userDto.getLastName())){
            userDetail.setLastName(userDto.getLastName());
        }
/*        if(Objects.nonNull(userDto.getPassword())){
            userDetail.setPassword(userDto.getPassword());
        }*/
        UserViewDto updateUser = new UserViewDto(userRepository.save(userDetail));
        return updateUser;
    }

    /**
     * Adds groups to user and updates userRole based on groups role.
     * @param userName
     * @param groups
     * @return
     */
    @Transactional
    @Override
    public UserViewDto addGroupsToUser(String userName, List<GroupDetailDto> groups) {
        UserDetail userDetail = getUserDetails(userName);
        String userRole = userDetail.getUserRole();
        userDetail = updateGroups(userDetail, groups);
        if(!userRole.equals(userDetail.getUserRole())){
            userDetail = userRepository.save(userDetail);
        }
        return new UserViewDto(userDetail);
    }

    /**
     * Deletes groups from user.
     * @param userName
     * @param groups
     * @return
     */
    @Transactional
    @Override
    public UserViewDto deleteGroupsFromUser(String userName, List<GroupDetailDto> groups) {
        UserDetail userDetail = getUserDetails(userName);
        String userRole = userDetail.getUserRole();
        String newUserRole = GroupRole.NON_ADMIN.toString();
        List<UserGroup> deleteGroups = new ArrayList<>();
        HashMap<String,String> groupsMap = new HashMap<>();
        for(GroupDetailDto group: groups){
            if(Objects.nonNull(group)){
                groupsMap.put(group.getGroupName(), group.getGroupName());
            }
        }
        List<UserGroup> userGroups = new ArrayList<UserGroup>(userDetail.getGroups());
        for(UserGroup userGroup: userGroups){
            if(groupsMap.containsKey(userGroup.getGroupDetail().getGroupName())){
                deleteGroups.add(userGroup);
                userDetail.getGroups().remove(userGroup);
            }else{
                if(userRole.equals(GroupRole.ADMIN.toString()) &&
                        userGroup.getGroupDetail().getGroupRole().equals(GroupRole.ADMIN.toString())){
                    newUserRole = GroupRole.ADMIN.toString();
                }
            }
        }
        deleteGroupRelation(deleteGroups);
        if(!newUserRole.equals(userRole)){
            userDetail.setUserRole(newUserRole);
            userDetail = userRepository.save(userDetail);
        }
        return new UserViewDto(userDetail);
    }

    /**
     * Fetches latest userDetails from db
     * @param userName
     * @return
     */
    private UserDetail getUserDetails(String userName){
        UserDetail userDetail = userRepository.findUserByUserName(userName);
        if(userDetail == null){
            throw new OpenApiResourceNotFoundException("User not found ::" +  userName);
        }
        return userDetail;
    }

    /**
     * Deletes Users relations associated with the group
     * @param deleteGroups
     */
    private void  deleteGroupRelation(List<UserGroup> deleteGroups){
        for(UserGroup userGroup: deleteGroups){
            userGroupRepository.deleteByGroupID(userGroup.getGroupDetail().getId());
        }
    }

    /**
     * Updates the groups for a user
     * @param userDetail
     * @param groupDtos
     * @return UserDetail
     */
    private UserDetail updateGroups(UserDetail userDetail, List<GroupDetailDto> groupDtos){
        UserGroup userGroup;
        List<UserGroup> existingGroups = userDetail.getGroups();
        for(GroupDetailDto groupDto: filterGroups(existingGroups, groupDtos)){
            GroupDetail groupDetail = groupRepository.findGroupDetailByGroupName(groupDto.getGroupName());
            if(Objects.nonNull(groupDetail)){
                if(userDetail.getUserRole().equals(GroupRole.NON_ADMIN.toString()) && groupDetail.getGroupRole().equals(GroupRole.ADMIN.toString())){
                    userDetail.setUserRole(GroupRole.ADMIN.toString());
                }
                userGroup = new UserGroup();
                userGroup.setGroupDetail(groupDetail);
                userGroup.setUserDetail(userDetail);
                userGroupRepository.save(userGroup);
                userDetail.getGroups().add(userGroup);
            }
        }
        return userDetail;
    }

    /**
     * This functionality is to ensure uniqueness of groups to users
     * @param existingGroups
     * @param groupDtos
     * @return List<GroupDto>
     */
    private List<GroupDetailDto> filterGroups(List<UserGroup> existingGroups, List<GroupDetailDto> groupDtos){
        List<GroupDetailDto> filterGroups = new ArrayList<>();
        if(groupDtos != null){
            HashMap<String, UserGroup> existingGroupsHash = new HashMap<>();
            HashMap<String, String> uniqueGroups = new HashMap<>();
            if(Objects.nonNull(existingGroups )){
                for(UserGroup userGroup: existingGroups){
                    existingGroupsHash.put(userGroup.getGroupDetail().getGroupName(), userGroup);
                }
            }
            for(GroupDetailDto groupDto: groupDtos){
                if(!existingGroupsHash.containsKey(groupDto.getGroupName()) && !uniqueGroups.containsKey(groupDto.getGroupName())){
                    uniqueGroups.put(groupDto.getGroupName(), groupDto.getGroupName());
                    filterGroups.add(groupDto);
                }
            }
        }
        return filterGroups;
    }

    /**
     * Delete user if its available.
     * @param userName
     * @return
     */
    @Transactional
    @Override
    public ApiResponseDto deleteUser(String userName) {
        ApiResponseDto customMessageDto = new ApiResponseDto();
        deleteGroupRelation(userName);
        int count = userRepository.deleteByUserName(userName);
        if(count >= 1){
            log.info(userName + " User found and deleted, number of rows deleted:" + count);
            customMessageDto.setHttpStatus(HttpStatus.OK);
            customMessageDto.setMessage("User found and deleted.");
            customMessageDto.setStatus(true);
        }else if(count == 0){
            log.info(userName + " User not found to delete, number of rows deleted:" + count);
            customMessageDto.setHttpStatus(HttpStatus.NOT_FOUND);
            customMessageDto.setMessage("User not found to delete.");
            customMessageDto.setStatus(false);
        }
        return customMessageDto;
    }

    /**
     * Deletes Relation between group and user
     * @param userName
     * @return count
     */
    private int deleteGroupRelation(String userName){
        UserDetail userDetail = userRepository.findUserByUserName(userName);
        int count = 0;
        if(Objects.nonNull(userDetail)){
            for(UserGroup userGroup: userDetail.getGroups()){
                userGroupRepository.deleteById(userGroup.getId());
                count++;
            }
        }
        return count;
    }

    /**
     * Get all users
     * @return
     */
    @Override
    public List<UserViewDto> getUsers() {
        List<UserDetail> users = userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user, UserViewDto.class)).collect(Collectors.toList());
    }

    /**
     *
     * @param value The value to check for
     * @param fieldName The name of the field for which to check if the value exists
     * @return
     * @throws UnsupportedOperationException
     */
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
