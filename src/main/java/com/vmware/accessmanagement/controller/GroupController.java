package com.vmware.accessmanagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.accessmanagement.dto.ApiResponseDto;
import com.vmware.accessmanagement.dto.GroupDetailDto;
import com.vmware.accessmanagement.dto.GroupUpdateDto;
import com.vmware.accessmanagement.dto.GroupUserDto;
import com.vmware.accessmanagement.service.GroupService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/groups")
@Log4j2
@Validated
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * API to Create Group
     * @param groupDto
     * @return ResponseEntity
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/createGroup", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity createGroup(@Valid @RequestBody GroupDetailDto groupDto) throws JsonProcessingException {
        ApiResponseDto apiResponseDto;
        try{
            apiResponseDto = groupService.createGroup(groupDto);
        }catch(Exception e){
            log.error(e.getMessage());
            throw e;
        }
        return ResponseEntity.status(apiResponseDto.getHttpStatus()).body(objectMapper.writeValueAsString(apiResponseDto));
    }

    /**
     * API to get Group along with users
     * @param groupName
     * @return GroupUserDto
     */
    @GetMapping(value = "/{groupName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getGroupWithUsers(@PathVariable String groupName) throws JsonProcessingException {
        log.info("Get Group and users with Group Name -> " + groupName);
        GroupUserDto groupUserDto = groupService.getGroupWithUsers(groupName);
        if(Objects.isNull(groupUserDto.getGroupName())){
            ApiResponseDto apiResponseDto = new ApiResponseDto(HttpStatus.NOT_FOUND, groupName+ " user not found.", false);
            return ResponseEntity.status(apiResponseDto.getHttpStatus()).body(objectMapper.writeValueAsString(apiResponseDto));
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(groupUserDto));
        }
    }

    /**
     * API to get All Groups
     * @return List<GroupDto>
     */
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GroupDetailDto> getGroups() {
        return groupService.getGroups();
    }

    /**
     * API to update Group
     * @param groupName
     * @param groupUpdateDto
     * @return ResponseEntity
     * @throws JsonProcessingException
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{groupName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateGroupDetail(@PathVariable String groupName, @Valid @RequestBody GroupUpdateDto groupUpdateDto) throws JsonProcessingException {
        log.info("Update GroupDetail api call with Group Name : " + groupName);
        ApiResponseDto apiResponseDto = groupService.updateGroupDetail(groupName, groupUpdateDto);
        return ResponseEntity.status(apiResponseDto.getHttpStatus()).body(objectMapper.writeValueAsString(apiResponseDto));
    }

    /**
     * API to update Group
     * @param groupName
     * @param userNames
     * @return ResponseEntity
     * @throws JsonProcessingException
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{groupName}/users/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addGroupUsers(@PathVariable String groupName, @Valid @RequestBody List<String> userNames) throws JsonProcessingException {
        log.info("Add users to Group, GroupName: " + groupName);
        ApiResponseDto apiResponseDto = groupService.addGroupUsers(groupName, userNames);
        return ResponseEntity.status(apiResponseDto.getHttpStatus()).body(objectMapper.writeValueAsString(apiResponseDto));
    }

    /**
     * API to update Group
     * @param groupName
     * @param userNames
     * @return ResponseEntity
     * @throws JsonProcessingException
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{groupName}/users/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteGroupUsers(@PathVariable String groupName, @Valid @RequestBody List<String> userNames) throws JsonProcessingException {
        log.info("Delete users from Group, GroupName: " + groupName);
        ApiResponseDto apiResponseDto = groupService.deleteGroupUsers(groupName, userNames);
        return ResponseEntity.status(apiResponseDto.getHttpStatus()).body(objectMapper.writeValueAsString(apiResponseDto));
    }

    /**
     * API to delete Group
     * @param groupName
     * @return ResponseEntity
     * @throws JsonProcessingException
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{groupName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteGroupDetail(@PathVariable String groupName) throws JsonProcessingException {
        log.info("Delete GroupDetail api call with Group Name : " + groupName);
        ApiResponseDto apiResponseDto = groupService.deleteGroup(groupName);
        return ResponseEntity.status(apiResponseDto.getHttpStatus()).body(objectMapper.writeValueAsString(apiResponseDto));
    }
}
