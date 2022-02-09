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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    /**
     * API to get all Users
     * @return List of UserViewDTO
     */
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserViewDto> getUsers() {
        return userService.getUsers();
    }

    /**
     * API to Create User
     * @param userDto
     * @return ResponseEntity
     * @throws JsonProcessingException
     */
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

    /**
     * API to get Users along with groups
     * @param userName
     * @return UserViewDto
     */
    @GetMapping(value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUserWithGroups(@PathVariable String userName) throws JsonProcessingException {
        log.info("Get users and groups with User Name -> " + userName);
        UserViewDto userViewDto = userService.getUserWithGroups(userName);
        if(Objects.isNull(userViewDto.getUserName())){
            ApiResponseDto apiResponseDto = new ApiResponseDto(HttpStatus.NOT_FOUND, userName+ " user not found.", false);
            return ResponseEntity.status(apiResponseDto.getHttpStatus()).body(objectMapper.writeValueAsString(apiResponseDto));
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(userViewDto));
        }
    }

    /**
     * API to update users
     * @param userName
     * @param userDto
     * @return UserViewDto
     * @throws JsonProcessingException
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserViewDto updateUserDetail(@PathVariable String userName, @Valid @RequestBody UserDto userDto) throws JsonProcessingException {
        log.info("Update User with userName: " + userName);
        return userService.updateUserAndUserGroups(userDto);
    }

    /**
     * API to delete user
     * @param userName
     * @return Response Entity
     * @throws JsonProcessingException
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteUserDetail(@PathVariable String userName) throws JsonProcessingException {
        log.info("Delete user with userName: " + userName);
        ApiResponseDto  apiResponseDto = userService.deleteUser(userName);

        return ResponseEntity.status(apiResponseDto.getHttpStatus()).body(objectMapper.writeValueAsString(apiResponseDto));
    }
}
