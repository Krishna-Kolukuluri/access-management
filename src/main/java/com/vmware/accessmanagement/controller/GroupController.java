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

    @GetMapping(value = "/{groupName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GroupUserDto getGroupWithUsers(@PathVariable String groupName) {
        log.info("Group Name -> " + groupName);
        return groupService.getGroupWithUsers(groupName);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GroupDto> getGroups() {
        return groupService.getGroups();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{groupName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateGroupDetail(@PathVariable String groupName, @Valid @RequestBody GroupUserDto groupUserDto) throws JsonProcessingException {
        log.info("groupName: " + groupName);
        ApiResponseDto apiResponseDto = groupService.updateGroup(groupUserDto);
        return ResponseEntity.status(apiResponseDto.getHttpStatus()).body(objectMapper.writeValueAsString(apiResponseDto));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{groupName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteGroupDetail(@PathVariable String groupName) throws JsonProcessingException {
        log.info("groupName: " + groupName);
        ApiResponseDto apiResponseDto = groupService.deleteGroup(groupName);
        return ResponseEntity.status(apiResponseDto.getHttpStatus()).body(objectMapper.writeValueAsString(apiResponseDto));
    }
}
