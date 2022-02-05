package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dao.UserRepository;
import com.vmware.accessmanagement.dto.UserDto;
import com.vmware.accessmanagement.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDto getUser(String userName) {
        return new UserDto(userRepository.findUserByUserName(userName));
    }

    @Override
    public UserDto createUser(User user) {

        return new UserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList());
    }
}
