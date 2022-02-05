package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dao.UserRepository;
import com.vmware.accessmanagement.dto.UserDto;
import com.vmware.accessmanagement.model.User;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
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
