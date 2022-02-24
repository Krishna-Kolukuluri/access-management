package com.vmware.accessmanagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.vmware.accessmanagement.dto.*;
import com.vmware.accessmanagement.service.UserService;
import com.vmware.accessmanagement.validator.ValidUserName;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@Log4j2
@Validated //class level validation like annotations on the path variable or even the request parameter directly
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * API to get all Users
     * @return List<UserViewDto>
     */
//    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
//    public List<UserViewDto> getUsers() {
//        return userService.getUsers();
//    }

    /**
     * API to get All Users with pagination
     * @return List<UserViewDto>
     */
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserViewDto> getGroups(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "20") Integer pageSize,
                                          @RequestParam(defaultValue = "userName") String sortBy,
                                          @RequestParam(defaultValue = "no") String all) {
        if(all.equalsIgnoreCase("yes")){
            return userService.getUsers();
        }
        return userService.getUsers(pageNo, pageSize, sortBy);
    }

    /**
     * API to Create User
     * @param userDetailDto
     * @return ResponseEntity
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/createUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createUser(@Valid @RequestBody UserDetailDto userDetailDto) throws JsonProcessingException {
        ApiResponseDto  apiResponseDto ;
        try{
            apiResponseDto = userService.createUser(userDetailDto);
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
    public ResponseEntity getUserWithGroups(@PathVariable @ValidUserName String userName) throws JsonProcessingException {
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
     * API to update user details
     * @param userName
     * @param userUpdateDto
     * @return UserViewDto
     * @throws JsonProcessingException
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserViewDto updateUserDetail(@PathVariable @ValidUserName String userName, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("Update User details : " + userName);
        return userService.updateUser(userName, userUpdateDto);
    }

    /**
     * API to partially update user details
     * @param userName
     * @param userDetailPatch
     * @return UserViewDto
     * @throws JsonProcessingException
     */
    @PatchMapping(path = "/{userName}", consumes="application/json-patch+json", produces=MediaType.APPLICATION_JSON_VALUE)
    public UserViewDto patchUserDetail(@PathVariable @ValidUserName String userName, @RequestBody JsonPatch userDetailPatch) throws JsonPatchException, JsonProcessingException {
        log.info("Patch User details : " + userName);
        return userService.partiallyUpdateUser(userName, userDetailPatch);
    }

    /**
     * API to Add users to group
     * @param userName
     * @param groupDto
     * @return UserViewDto
     * @throws JsonProcessingException
     */
    @RequestMapping(method = RequestMethod.POST, value = "/{userName}/groups/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserViewDto addGroupsToUser(@PathVariable @ValidUserName String userName, @Valid @RequestBody List<GroupDetailDto> groupDto) {
        log.info("Add Groups to User : " + userName);
        return userService.addGroupsToUser(userName, groupDto);
    }

    /**
     * API to delete groups from user
     * @param userName
     * @param groupDto
     * @return UserViewDto
     * @throws JsonProcessingException
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{userName}/groups/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserViewDto deleteGroupsFromUser(@PathVariable @ValidUserName String userName, @Valid @RequestBody List<GroupDetailDto> groupDto) {
        log.info("Delete Groups from User : " + userName);
        return  userService.deleteGroupsFromUser(userName, groupDto);
    }

    /**
     * API to delete user
     * @param userName
     * @return Response Entity
     * @throws JsonProcessingException
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteUser(@PathVariable @ValidUserName String userName) throws JsonProcessingException {
        log.info("Delete user with userName: " + userName);
        ApiResponseDto  apiResponseDto = userService.deleteUser(userName);

        return ResponseEntity.status(apiResponseDto.getHttpStatus()).body(objectMapper.writeValueAsString(apiResponseDto));
    }
}
