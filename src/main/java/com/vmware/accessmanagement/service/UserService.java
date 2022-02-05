package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.UserDto;
import com.vmware.accessmanagement.model.User;

import java.util.List;

public interface UserService {
    UserDto getUser(String userName);
    List<UserDto> getUsers();

    UserDto createUser(User user);

}
