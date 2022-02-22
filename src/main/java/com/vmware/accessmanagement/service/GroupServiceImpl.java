package com.vmware.accessmanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.vmware.accessmanagement.dto.*;
import com.vmware.accessmanagement.model.*;
import com.vmware.accessmanagement.repository.GroupRepository;
import com.vmware.accessmanagement.repository.UserGroupRepository;
import com.vmware.accessmanagement.repository.UserRepository;
import lombok.extern.log4j.Log4j2;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Group service to add, update, delete groups and add, delete users from group.
 */
@Service
@Log4j2
public class GroupServiceImpl implements GroupService {
    private static final String ADMIN_ALL = "ADMIN_ALL";
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create new group is not already available in db
     * @param groupDto
     * @return
     */
    @Override
    public ApiResponseDto createGroup(GroupDetailDto groupDto) {
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

    /**
     * fetches group + all users associated with that group from db
     * @param groupName
     * @return
     */
    @Override
    public GroupUserDto getGroupWithUsers(String groupName) {
        GroupDetail groupDetail= groupRepository.findGroupDetailByGroupName(groupName);
        if(Objects.isNull(groupDetail)){
            return new GroupUserDto();
        }
        return new GroupUserDto(groupDetail);
    }

    /**
     * fetches all groups details from db.
     * @return
     */
    @Override
    public List<GroupDetailDto> getGroups() {
        List<GroupDetail> groups = groupRepository.findAll();
        return groups.stream().map(group -> new GroupDetailDto(group)).collect(Collectors.toList());
    }

    /**
     * Updates group details with only fields that are allowed to change after creating group.
     * @param groupName
     * @param groupDto
     * @return
     */
    @Transactional
    @Override
    public ApiResponseDto updateGroupDetail(String groupName, GroupUpdateDto groupDto){
        GroupDetail groupDetail = groupRepository.findGroupDetailByGroupName(groupName);
        if(groupDetail == null){
            throw new OpenApiResourceNotFoundException("Group not found ::" +  groupName);
        }
        groupDetail.setGroupDescription(groupDto.getGroupDescription());
        groupRepository.save(groupDetail);
        return new ApiResponseDto(HttpStatus.OK,"Updated Group: '" + groupName +"'",  true);
    }

    /**
     * Updates group details with only fields that are allowed to change after creating group.
     * @param groupName
     * @param groupPatch
     * @return
     */
    @Transactional
    @Override
    public ApiResponseDto patchGroupDetail(String groupName, JsonPatch groupPatch) throws JsonPatchException, JsonProcessingException {
        GroupDetail groupDetail = groupRepository.findGroupDetailByGroupName(groupName);
        if(groupDetail == null){
            throw new OpenApiResourceNotFoundException("Group not found ::" +  groupName);
        }
        groupDetail = applyPatchToGroup(groupPatch, groupDetail);
        groupRepository.save(groupDetail);
        return new ApiResponseDto(HttpStatus.OK,"Updated Group: '" + groupName +"'",  true);
    }
    private GroupDetail applyPatchToGroup(JsonPatch groupPatch, GroupDetail groupDetail) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = groupPatch.apply(objectMapper.convertValue(groupDetail, JsonNode.class));
        return objectMapper.treeToValue(patched, GroupDetail.class);
    }

    /**
     * Adds users to group and updates userRole is group is of type ADMIN.
     * @param groupName
     * @param userNames
     * @return
     */
    @Override
    public ApiResponseDto addUsersToGroup(String groupName, List<String> userNames) {
        GroupDetail groupDetail = getGroupDetails(groupName);
        groupDetail = updateGroupUsers(groupDetail, userNames);
        if(Objects.isNull(groupDetail)){
            return new ApiResponseDto(HttpStatus.NOT_FOUND,"Group update failed as user(s) not found, GroupName: '" +
                    groupName +"'",  false);
        }
        return new ApiResponseDto(HttpStatus.OK,"Added available users to Group: '" + groupName +"'",  true);
    }

    /**
     * deletes users from group and updates users role if remaining groups on each user are non_admin and current group is Admin
     * @param groupName
     * @param userNames
     * @return
     */
    @Override
    public ApiResponseDto deleteUsersFromGroup(String groupName, List<String> userNames) {
        GroupDetail groupDetail = getGroupDetails(groupName);
        if(Objects.nonNull(groupDetail.getUsers())){
            List<UserGroup> existingUsers = new ArrayList<>(groupDetail.getUsers());
            HashMap<String, String> userMap = new HashMap<>();
            for(String userName: userNames){
                if(!userMap.containsKey(userName)){
                    userMap.put(userName,userName);
                }
            }
            for(UserGroup userGroup: existingUsers){
                if(userMap.containsKey(userGroup.getUserDetail().getUserName())){
                    updateUserRole(groupName, groupDetail.getGroupRole(), userGroup.getUserDetail());
                    userGroupRepository.deleteById(userGroup.getId());
                    groupDetail.getUsers().remove(userGroup);
                }
            }
        }
        return new ApiResponseDto(HttpStatus.OK,"Deleted available users from Group: '" + groupName +"'",  true);
    }

    /**
     * updates users role if remaining groups on each user are non_admin and current group is Admin
     * @param groupName
     * @param groupRole
     * @param userDetail
     */
    private void updateUserRole(String groupName, String groupRole, UserDetail userDetail){
        if(groupRole.equals(GroupRole.ADMIN.toString())){
            String updatedUserRole = GroupRole.NON_ADMIN.toString();
            for(UserGroup userGroup: userDetail.getGroups()){
                if(!userGroup.getGroupDetail().getGroupName().equals(groupName) &&
                        userGroup.getGroupDetail().getGroupRole().equals(GroupRole.ADMIN.toString())){
                    updatedUserRole = GroupRole.ADMIN.toString();
                    break;
                }
            }
            if(updatedUserRole.equals(GroupRole.NON_ADMIN.toString())){
                userDetail.setUserRole(GroupRole.NON_ADMIN.toString());
                userRepository.save(userDetail);
            }
        }
    }

    /**
     * fetches latest group details from db
     * @param groupName
     * @return
     */
    private GroupDetail getGroupDetails(String groupName){
        GroupDetail groupDetail = groupRepository.findGroupDetailByGroupName(groupName);
        if(groupDetail == null){
            throw new OpenApiResourceNotFoundException("Group not found ::" +  groupName);
        }
        return groupDetail;
    }


    /**
     * Updates users in a group
     * @param groupDetail
     * @param userNames
     * @return GroupDetail
     */
    private GroupDetail updateGroupUsers(GroupDetail groupDetail, List<String> userNames){
        UserGroup userGroup;
        List<UserGroup> existingUsers = groupDetail.getUsers();
        for(String userName: filterUsers(existingUsers, userNames)){
            userGroup = new UserGroup();
            UserDetail userDetail = userRepository.findUserByUserName(userName);
            if(Objects.nonNull(userDetail)){
                if(groupDetail.getGroupRole().equals(GroupRole.ADMIN.toString()) &&
                        userDetail.getUserRole().equals(GroupRole.NON_ADMIN.toString())){
                    userDetail.setUserRole(GroupRole.ADMIN.toString());
                    userRepository.save(userDetail);
                }
                userGroup.setUserDetail(userDetail);
                userGroup.setGroupDetail(groupDetail);
                userGroupRepository.save(userGroup);
                if(Objects.isNull(groupDetail.getUsers())){
                    groupDetail.setUsers(new ArrayList<>());
                }
                groupDetail.getUsers().add(userGroup);
            }else{
                return null;
            }
        }
        return groupDetail;
    }

    /**
     * This functionality is to ensure uniqueness of users to groups
     * @param existingUsers
     * @param userNames
     * @return List<GroupDto>
     */
    private List<String> filterUsers(List<UserGroup> existingUsers, List<String>  userNames){
        List<String> filteredUsers = new ArrayList<>();
        if(Objects.nonNull(userNames)){
            HashMap<String, UserGroup> existingUsersGroup = new HashMap<>();
            HashMap<String, String> userNamesMap = new HashMap<>();
            if(Objects.nonNull(existingUsers)){
                for(UserGroup userGroup: existingUsers){
                    existingUsersGroup.put(userGroup.getUserDetail().getUserName(), userGroup);
                }
            }
            for(String userName: userNames){
                if(!existingUsersGroup.containsKey(userName) && !userNamesMap.containsKey(userName)){
                    userNamesMap.put(userName,userName);
                    filteredUsers.add(userName);
                }
            }
        }
        return filteredUsers;
    }

    /**
     *
     * @param groupName
     * @return
     */
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
