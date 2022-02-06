package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.CustomMessageDto;
import com.vmware.accessmanagement.dto.UserDto;
import com.vmware.accessmanagement.model.UserDetail;
import com.vmware.accessmanagement.validator.FieldValueExists;

import java.util.List;

public interface UserService extends FieldValueExists {
    UserDto getUser(String userName);
    List<UserDto> getUsers();
    UserDto createUser(UserDetail user);
    UserDto updateUser(UserDetail userDetail);
    CustomMessageDto deleteUser(String userName);
}
