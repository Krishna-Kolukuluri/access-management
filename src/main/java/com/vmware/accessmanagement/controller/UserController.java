package com.vmware.accessmanagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.accessmanagement.dto.ApiResponseDto;
import com.vmware.accessmanagement.dto.UserDto;
import com.vmware.accessmanagement.dto.UserViewDto;
import com.vmware.accessmanagement.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ObjectMapper objectMapper;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserViewDto> getUsers(Map<String, Object> model) {
        return userService.getUsers();
    }

    @PostMapping(value = "/createUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createUser(@Valid @RequestBody UserDto userDto) throws JsonProcessingException {
        ApiResponseDto  apiResponseDto = new ApiResponseDto();
        try{
            apiResponseDto = userService.createUser(userDto);
        }catch(Exception e){
            log.error(e.getMessage());
            throw e;
        }
        return ResponseEntity.status(apiResponseDto.getHttpStatus()).body(objectMapper.writeValueAsString(apiResponseDto));

    }

    @GetMapping(value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserViewDto getUserWithGroups(@PathVariable String userName) {
        log.info("User Name -> " + userName);
        return userService.getUserWithGroups(userName);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserViewDto updateUserDetail(@PathVariable String userName, @Valid @RequestBody UserDto userDto) throws JsonProcessingException {
        log.info("userName: " + userName);
        return userService.updateUserAndUserGroups(userDto);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteUserDetail(@PathVariable String userName) throws JsonProcessingException {
        log.info("userName: " + userName);
        ApiResponseDto  apiResponseDto = userService.deleteUser(userName);

        return ResponseEntity.status(apiResponseDto.getHttpStatus()).body(objectMapper.writeValueAsString(apiResponseDto));
    }
}
