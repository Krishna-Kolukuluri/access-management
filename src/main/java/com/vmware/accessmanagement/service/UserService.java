package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.CustomMessageDto;
import com.vmware.accessmanagement.dto.UserDto;
import com.vmware.accessmanagement.dto.UserViewDto;
import com.vmware.accessmanagement.model.UserDetail;
import com.vmware.accessmanagement.validator.FieldValueExists;

import java.util.List;

public interface UserService extends FieldValueExists {
    UserViewDto getUserWithGroups(String userName);
    List<UserViewDto> getUsers();
    UserViewDto createUser(UserDto user);
    UserViewDto updateUser(UserDto userDto);
    CustomMessageDto deleteUser(String userName);
}
