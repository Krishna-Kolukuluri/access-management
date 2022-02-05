package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.UserDto;
import com.vmware.accessmanagement.model.User;
import com.vmware.accessmanagement.validator.FieldValueExists;

import java.util.List;

public interface UserService extends FieldValueExists {
    UserDto getUser(String userName);
    List<UserDto> getUsers();

    UserDto createUser(User user);

}
