package com.vmware.accessmanagement.controller;

import com.vmware.accessmanagement.dto.CustomMessageDto;
import com.vmware.accessmanagement.dto.GroupDto;
import com.vmware.accessmanagement.dto.GroupUserDto;
import com.vmware.accessmanagement.model.GroupDetail;
import com.vmware.accessmanagement.service.GroupService;
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

@RestController
@RequestMapping("/groups")
@Log4j2
@Validated
public class GroupController {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GroupService groupService;

    @PostMapping(value = "/createGroup", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity createGroup(@Valid @RequestBody GroupDto groupDto) {
        try{
            groupService.createGroup(groupDto);
        }catch(Exception e){
            log.error(e.getMessage());
            throw e;
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Created Group with GroupName: '" + groupDto.getGroupName() +"'");
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
    public GroupDto updateGroupDetail(@PathVariable String groupName, @Valid @RequestBody GroupDto groupDto){
        log.info("groupName: " + groupName);
        return groupService.updateGroup(modelMapper.map(groupDto, GroupDetail.class));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{groupName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomMessageDto deleteUserDetail(@PathVariable String groupName){
        log.info("groupName: " + groupName);
        return groupService.deleteGroup(groupName);
    }
}
