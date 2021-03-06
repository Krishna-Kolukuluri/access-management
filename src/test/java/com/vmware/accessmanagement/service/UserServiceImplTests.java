package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dto.*;
import com.vmware.accessmanagement.model.*;
import com.vmware.accessmanagement.repository.GroupRepository;
import com.vmware.accessmanagement.repository.UserGroupRepository;
import com.vmware.accessmanagement.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springdoc.api.OpenApiResourceNotFoundException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Log4j2
public class UserServiceImplTests {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    static UserDetail userDetail = new UserDetail();
    static GroupDetail groupDetail = new GroupDetail();
    //static UserGroupDto userGroupDto = new UserGroupDto();
    static UserDetailDto userDetailDto = new UserDetailDto();
    static UserUpdateDto userUpdateDto = new UserUpdateDto();
    static List<UserGroup> groups = new ArrayList<>();
    static UserDetail updatedUserDetail = new UserDetail();

    @BeforeEach
    public void setup() throws ParseException {
        MockitoAnnotations.openMocks(this);
        String dob="31-12-2010";
        Date date=new SimpleDateFormat("DD-MM-YYYY"). parse(dob);
        userDetail.setFirstName("Krishna");
        userDetail.setLastName("Kolukuluri");
        userDetail.setUserName("Krishna.Kolukuluri");
        userDetail.setUserRole(GroupRole.ADMIN.toString());
        userDetail.setDob(date);
        userDetail.setAddress("111 Address Cary, NC");
        UserGroup group = new UserGroup();
        group.setUserDetail(userDetail);
        groupDetail.setGroupName("Admin-Group");
        groupDetail.setGroupRole(GroupRole.ADMIN.toString());
        groupDetail.setGroupPermission(GroupPermission.ALL.toString());
        groupDetail.setGroupDescription("Testing");
        group.setGroupDetail(groupDetail);
        group.setId(1L);
        groups.add(group);
        userDetail.setGroups(groups);

        updatedUserDetail = userDetail;
        updatedUserDetail.setUserName("Krishna.Kolukuluri.updated");

        userDetailDto.setFirstName("Krishna");
        userDetailDto.setLastName("Kolukuluri");
        userDetailDto.setUserName("Krishna.Kolukuluri");
        userDetailDto.setUserRole(GroupRole.ADMIN.toString());
        userDetailDto.setDob(date);
        userDetailDto.setAddress("111 Address Cary, NC");

        userUpdateDto.setFirstName("Krishna");
        userUpdateDto.setLastName("Kolukuluri");
        //userUpdateDto.setUserName("Krishna.Kolukuluri");
        userUpdateDto.setUserRole(GroupRole.ADMIN.toString());
        userUpdateDto.setDob(date);
        userUpdateDto.setAddress("111 Address Cary, NC");
    }

    @Test
    public void test_CreatedUser() throws ParseException {
        when(modelMapper.map(any(),any())).thenReturn(userDetail);
        when(userRepository.save(any())).thenReturn(userDetail);
        userService.createUser(userDetailDto);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void test_GetUsers(){
        List<UserDetail> users = new ArrayList<>();
        users.add(userDetail);
        when(userRepository.findAll()).thenReturn(users);
        List<UserViewDto> result = userService.getUsers();
        assertEquals(1,result.size());
    }

    @Test
    public void test_DeleteUser(){
        when(userRepository.deleteByUserName(anyString())).thenReturn(1);
        when(userRepository.findUserByUserName(anyString())).thenReturn(userDetail);
        ApiResponseDto message = userService.deleteUser(userDetail.getUserName());
        assertEquals("User found and deleted.",message.getMessage());
    }

    @Test
    public void test_DeleteUser_NotFound(){
        when(userRepository.deleteByUserName(anyString())).thenReturn(0);
        ApiResponseDto message = userService.deleteUser(userDetail.getUserName());
        assertEquals("User not found to delete.",message.getMessage());
    }

    @Test
    public void test_GetUserWithGroups(){
        when(userRepository.findUserByUserName(anyString())).thenReturn(userDetail);
        UserViewDto resultDto = userService.getUserWithGroups(userDetail.getUserName());
        assertNotNull(resultDto);
    }

    @Test
    public void test_UpdateUser(){
        when(userRepository.findUserByUserName(anyString())).thenReturn(userDetail);
        when(userRepository.save(any())).thenReturn(updatedUserDetail);
        when(userGroupRepository.deleteByGroupID(anyLong())).thenReturn(1);
        when(groupRepository.findGroupDetailByGroupName(anyString())).thenReturn(groupDetail);
        UserViewDto resultDto = userService.updateUser("Krishna.Kolukuluri", userUpdateDto);
        assertNotNull(resultDto);
        assertTrue(resultDto.getUserName().contains(updatedUserDetail.getUserName()));
    }

    @Test
    public void test_UpdateUser_Role(){
        when(userRepository.findUserByUserName(anyString())).thenReturn(userDetail);
        when(userRepository.save(any())).thenReturn(updatedUserDetail);
        when(userGroupRepository.deleteByGroupID(anyLong())).thenReturn(1);
        when(groupRepository.findGroupDetailByGroupName(anyString())).thenReturn(groupDetail);
        userUpdateDto.setUserRole(GroupRole.NON_ADMIN.toString());
        UserViewDto resultDto = userService.updateUser("Krishna.Kolukuluri", userUpdateDto);
        assertNotNull(resultDto);
        assertTrue(resultDto.getUserName().contains(updatedUserDetail.getUserName()));
    }

    @Test
    public void test_UpdateUser_Role2(){
        userDetail.setUserRole(GroupRole.NON_ADMIN.toString());
        when(userRepository.findUserByUserName(anyString())).thenReturn(userDetail);
        when(userRepository.save(any())).thenReturn(updatedUserDetail);
        when(userGroupRepository.deleteByGroupID(anyLong())).thenReturn(1);
        when(groupRepository.findGroupDetailByGroupName(anyString())).thenReturn(groupDetail);
        UserViewDto resultDto = userService.updateUser("Krishna.Kolukuluri", userUpdateDto);
        assertNotNull(resultDto);
        assertTrue(resultDto.getUserName().contains(updatedUserDetail.getUserName()));
    }

    @Test
    public void test_UpdateUser_NotFound(){
        when(userRepository.findUserByUserName(anyString())).thenReturn(null);
        Exception exception = assertThrows(OpenApiResourceNotFoundException.class, () -> {
            userService.updateUser("Krishna.Kolukuluri", userUpdateDto);
        });
        assertTrue(exception.getMessage().contains("User not found"));

    }

    @Test
    public void test_FieldValueExists(){
        when(userRepository.existsByUserName(anyString())).thenReturn(true);
        String value = "KrishnaTest";
        assertTrue(userService.fieldValueExists(value,"userName"));
    }

    @Test
    public void test_FieldValueExists_null(){
        String value = null;
        assertFalse(userService.fieldValueExists(value,"userName"));
    }

    @Test()
    public void test_FieldValueExists_Exception(){
        when(userRepository.existsByUserName(anyString())).thenReturn(true);
        String value = "KrishnaTest";
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            userService.fieldValueExists(value,"firstName");
        });
        assertTrue(exception.getMessage().contains("Field name not supported"));
    }

}
