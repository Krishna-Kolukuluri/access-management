package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.CustomMessageDto;
import com.vmware.accessmanagement.model.UserDetail;
import com.vmware.accessmanagement.repository.UserRepository;
import com.vmware.accessmanagement.dto.UserDto;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Assert;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    public UserDto updateUser(UserDetail userDetail) {
        UserDetail userDetail1 = userRepository.findUserByUserName(userDetail.getUserName());
        if(userDetail1 == null){
            throw new OpenApiResourceNotFoundException("userName not found ::" +  userDetail.getUserName());
        }
        userDetail1.setUserRole(userDetail.getUserRole());
        userDetail1.setAddress(userDetail.getAddress());
        userDetail1.setDob(userDetail.getDob());
        userDetail1.setFirstName(userDetail.getFirstName());
        userDetail1.setLastName(userDetail.getLastName());
        userDetail1.setPassword(userDetail.getPassword());
        UserDto updateUser = new UserDto(userRepository.save(userDetail1));
        return updateUser;
    }

    @Transactional
    @Override
    public CustomMessageDto deleteUser(String userName) {
        CustomMessageDto customMessageDto = new CustomMessageDto();
        int count = userRepository.deleteByUserName(userName);
        if(count >= 1){
            customMessageDto.setMessage("User found and deleted, number of rows deleted:" + count);
            customMessageDto.setStatus(true);
        }else if(count == 0){
            customMessageDto.setMessage("User not found to delete, number of rows deleted:" + count);
            customMessageDto.setStatus(false);
        }
        return customMessageDto;
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
