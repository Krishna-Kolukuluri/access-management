package com.vmware.accessmanagement.controller;

import com.vmware.accessmanagement.dto.CustomMessageDto;
import com.vmware.accessmanagement.dto.UserDto;
import com.vmware.accessmanagement.dto.UserViewDto;
import com.vmware.accessmanagement.model.UserDetail;
import com.vmware.accessmanagement.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Log4j2
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserViewDto> getUsers(Map<String, Object> model) {
        return userService.getUsers();
    }

    @PostMapping(value = "/createUser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity createUser(@Valid @RequestBody UserDto userDto) {
        try{
            userService.createUser(userDto);
        }catch(Exception e){
            log.error(e.getMessage());
            throw e;
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Created User with User Name: '" + userDto.getUserName() +"'");
    }

    @GetMapping(value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserViewDto getUserWithGroups(@PathVariable String userName) {
        log.info("User Name -> " + userName);
        return userService.getUserWithGroups(userName);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserViewDto updateUserDetail(@PathVariable String userName, @Valid @RequestBody UserDto userDto){
        log.info("userName: " + userName);
        userService.updateUser(userDto);
        return userService.getUserWithGroups(userName);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomMessageDto deleteUserDetail(@PathVariable String userName){
        log.info("userName: " + userName);
        return userService.deleteUser(userName);
    }
}
