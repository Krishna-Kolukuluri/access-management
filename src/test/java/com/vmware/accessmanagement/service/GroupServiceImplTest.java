package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.ApiResponseDto;
import com.vmware.accessmanagement.dto.GroupDto;
import com.vmware.accessmanagement.dto.GroupUserDto;
import com.vmware.accessmanagement.dto.UserInGroupDto;
import com.vmware.accessmanagement.model.*;
import com.vmware.accessmanagement.repository.GroupRepository;
import com.vmware.accessmanagement.repository.UserGroupRepository;
import com.vmware.accessmanagement.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.aspectj.bridge.IMessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Log4j2
//@Tag("Unit")
public class GroupServiceImplTest {
    static GroupDto groupDto = new GroupDto();
    static GroupDetail groupDetail = new GroupDetail();
    static GroupUserDto groupUserDto = new GroupUserDto();
    @InjectMocks
    private GroupServiceImpl groupService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Validator validator;

    @BeforeEach
    public void setup() throws ParseException {
        MockitoAnnotations.openMocks(this);
        groupDto.setGroupName("Admin_Group");
        groupDto.setGroupRole(GroupRole.ADMIN.toString());
        groupDto.setGroupPermission(GroupPermission.ALL.toString());
        groupDto.setGroupDescription("Testing");

        groupDetail.setGroupPermission(GroupPermission.ALL.toString());
        groupDetail.setGroupDescription("Testing");
        groupDetail.setGroupName("Admin_Group");
        groupDetail.setGroupRole(GroupRole.ADMIN.toString());
        List<UserGroup> users = new ArrayList<>();
        UserGroup user = new UserGroup();
        UserDetail userDetail = new UserDetail();
        String dob="31-12-2010";
        Date date=new SimpleDateFormat("DD-MM-YYYY"). parse(dob);
        userDetail.setFirstName("Krishna");
        userDetail.setLastName("Kolukuluri");
        userDetail.setUserName("Krishna.Kolukuluri");
        userDetail.setUserRole(GroupRole.ADMIN.toString());
        userDetail.setDob(date);
        userDetail.setAddress("111 Address Cary, NC");
        user.setUserDetail(userDetail);
        user.setGroupDetail(groupDetail);
        users.add(user);
        groupDetail.setUsers(users);

        groupUserDto.setGroupName("Admin_Group");
        groupUserDto.setGroupDescription("Testing Updated");
        groupUserDto.setGroupPermission(GroupPermission.ALL.toString());
        groupUserDto.setGroupRole(GroupRole.ADMIN.toString());
        List<UserInGroupDto> userInGroupDtos = new ArrayList<>();
        UserInGroupDto userInGroupDto = new UserInGroupDto();
        userInGroupDto.setUserRole(GroupRole.ADMIN.toString());
        userInGroupDto.setUserName("Krishna.Kolukuluri");
        userInGroupDto.setLastName("Kolukuluri");
        userInGroupDto.setFirstName("krishna-New");
        userInGroupDtos.add(userInGroupDto);
        groupUserDto.setUsers(userInGroupDtos);
    }

    @Test
    public void test_CreateGroup(){
        when(modelMapper.map(any(),any())).thenReturn(groupDetail);
        when(groupRepository.save(any())).thenReturn(groupDetail);
        ApiResponseDto result = groupService.createGroup(groupDto);
        verify(groupRepository, times(1)).save(any());
    }

    @Test
    public void text_getGroupWithUsers(){
        when(groupRepository.findGroupDetailByGroupName(anyString())).thenReturn(groupDetail);
        GroupUserDto groupUserDTO = groupService.getGroupWithUsers("Admin_Group");
        assertEquals(1,groupUserDTO.getUsers().size());
    }

    @Test
    public void test_getGroupWithUsers_NoGroup(){
        when(groupRepository.findGroupDetailByGroupName(anyString())).thenReturn(null);
        GroupUserDto groupUserDTO = groupService.getGroupWithUsers("Admin_Group");
        assertNull(groupUserDTO.getUsers());
    }

    @Test
    public void test_getGroups(){
        List<GroupDetail> groups = new ArrayList<>();
        groups.add(groupDetail);
        when(groupRepository.findAll()).thenReturn(groups);
        List<GroupDto> result = groupService.getGroups();
        assertEquals(1,result.size());
    }

    @Test
    public void test_updateGroup(){
        when(groupRepository.findGroupDetailByGroupName(anyString())).thenReturn(groupDetail);
        when(groupRepository.save(any())).thenReturn(groupDetail);
        ApiResponseDto result = groupService.updateGroup(groupUserDto);
    }

    @Test
    public void test_updateGroup_Exception(){
        when(groupRepository.findGroupDetailByGroupName(anyString())).thenReturn(null);
        Exception exception = assertThrows(OpenApiResourceNotFoundException.class, () -> {
            groupService.updateGroup(groupUserDto);
        });
        assertTrue(exception.getMessage().contains("Group not found"));
    }

    @Test
    public void test_updateGroup_ExceptionValidation(){
        groupUserDto.setGroupPermission(GroupPermission.READ.toString());
        when(groupRepository.findGroupDetailByGroupName(anyString())).thenReturn(groupDetail);
       Exception exception = assertThrows(ConstraintViolationException.class, () -> {
            groupService.updateGroup(groupUserDto);
        });
        assertTrue(exception.getMessage().contains("Changing Group permissions not allowed"));
    }

    @Test
    public void test_deleteGroup(){
        when(groupRepository.findGroupDetailByGroupName(anyString())).thenReturn(groupDetail);
        when(groupRepository.deleteByGroupName(anyString())).thenReturn(1);
        ApiResponseDto result = groupService.deleteGroup(groupDetail.getGroupName());
        assertTrue(result.getMessage().contains("Group found and deleted"));
    }

    @Test
    public void test_deleteGroup_AdminAll(){
        when(groupRepository.findGroupDetailByGroupName(anyString())).thenReturn(groupDetail);
        when(groupRepository.deleteByGroupName(anyString())).thenReturn(1);
        ApiResponseDto result = groupService.deleteGroup("ADMIN_ALL");
        assertTrue(result.getMessage().contains("ADMIN_ALL default group can't be deleted."));
    }

    @Test
    public void test_deleteGroup_NotFound(){
        when(groupRepository.findGroupDetailByGroupName(anyString())).thenReturn(groupDetail);
        when(groupRepository.deleteByGroupName(anyString())).thenReturn(0);
        ApiResponseDto result = groupService.deleteGroup(groupDetail.getGroupName());
        assertTrue(result.getMessage().contains("Group not found to delete"));
    }
}
