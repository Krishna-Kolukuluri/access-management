package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.ApiResponseDto;
import com.vmware.accessmanagement.dto.UserDto;
import com.vmware.accessmanagement.dto.UserViewDto;
import com.vmware.accessmanagement.validator.FieldValueExists;

import java.util.List;

public interface UserService extends FieldValueExists {
    UserViewDto getUserWithGroups(String userName);
    List<UserViewDto> getUsers();
    ApiResponseDto createUser(UserDto user);
    UserViewDto updateUserAndUserGroups(UserDto userDto);
    ApiResponseDto deleteUser(String userName);
}
