package com.vmware.accessmanagement.controller;

import com.vmware.accessmanagement.dto.UserDto;
import com.vmware.accessmanagement.exception.CreateUserException;
import com.vmware.accessmanagement.model.User;
import com.vmware.accessmanagement.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Log4j2
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping("/")
    public List<UserDto> getUsers(Map<String, Object> model) {
        return userService.getUsers();
    }

    @PostMapping(value = "/createUser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity createUser(@RequestBody UserDto userDto) {
        try{
            userService.createUser(modelMapper.map(userDto, User.class));
        }catch(Exception e){
            log.error(e.getMessage());
            throw new CreateUserException();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Created User" + userDto.getUserName());
    }

    @GetMapping(value = "/user/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto getUser(@PathVariable String userName) {
        log.info("User Name -> " + userName);
        return userService.getUser(userName);
    }
}
