package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.model.UserDetail;
import com.vmware.accessmanagement.repository.UserRepository;
import com.vmware.accessmanagement.dto.UserDto;
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
    public UserDto createUser(UserDetail userDetail) {
        return new UserDto(userRepository.save(userDetail));
    }

    @Override
    public List<UserDto> getUsers() {
        List<UserDetail> users = userRepository.findAll();
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
