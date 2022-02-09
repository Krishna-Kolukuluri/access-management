package com.vmware.accessmanagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.accessmanagement.dto.ApiResponseDto;
import com.vmware.accessmanagement.dto.GroupDto;
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
    public ResponseEntity createGroup(@Valid @RequestBody GroupDto groupDto) throws JsonProcessingException {
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
    public GroupUserDto getGroupWithUsers(@PathVariable String groupName) {
        log.info("Get all Group users with Group Name -> " + groupName);
        return groupService.getGroupWithUsers(groupName);
    }

    /**
     * API to get All Groups
     * @return List<GroupDto>
     */
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GroupDto> getGroups() {
        return groupService.getGroups();
    }

    /**
     * API to update Group
     * @param groupName
     * @param groupUserDto
     * @return ResponseEntity
     * @throws JsonProcessingException
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{groupName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateGroupDetail(@PathVariable String groupName, @Valid @RequestBody GroupUserDto groupUserDto) throws JsonProcessingException {
        log.info("Update GroupDetail api call with Group Name : " + groupName);
        ApiResponseDto apiResponseDto = groupService.updateGroup(groupUserDto);
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
