package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.*;
import com.vmware.accessmanagement.validator.FieldValueExists;

import java.util.List;

public interface UserService extends FieldValueExists {
    UserViewDto getUserWithGroups(String userName);
    List<UserViewDto> getUsers();
    ApiResponseDto createUser(UserDetailDto user);
    UserViewDto updateUser(String userName, UserUpdateDto userDto);
    UserViewDto addGroupsToUser(String userName, List<GroupDetailDto> userDto);
    UserViewDto deleteGroupsFromUser(String userName, List<GroupDetailDto> userDto);
    ApiResponseDto deleteUser(String userName);
}
