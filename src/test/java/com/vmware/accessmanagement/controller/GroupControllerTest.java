package com.vmware.accessmanagement.controller;

import com.vmware.accessmanagement.dto.ApiResponseDto;
import com.vmware.accessmanagement.dto.GroupDetailDto;
import com.vmware.accessmanagement.dto.UserDetailDto;
import com.vmware.accessmanagement.model.GroupPermission;
import com.vmware.accessmanagement.model.GroupRole;
import com.vmware.accessmanagement.service.GroupService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
@Transactional
public class GroupControllerTest extends BaseTest{
    static List<GroupDetailDto> groups = new ArrayList<>();
    static GroupDetailDto groupDto = new GroupDetailDto();
    private static UserDetailDto userDetailDto;
    @Mock
    private GroupService groupService;

    @BeforeEach
    public void setup() throws ParseException {
      MockitoAnnotations.openMocks(this);
      groupDto.setGroupName("Admin_Group");
      groupDto.setGroupRole(GroupRole.ADMIN.toString());
      groupDto.setGroupPermission(GroupPermission.ALL.toString());
      groupDto.setGroupDescription("Testing");

      String dob="31-12-2010";
      Date date=new SimpleDateFormat("DD-MM-YYYY"). parse(dob);
      userDetailDto = new UserDetailDto();
      userDetailDto.setFirstName("Krishna");
      userDetailDto.setLastName("Kolukuluri");
      userDetailDto.setUserName("Krishna.Kolukuluri");
      userDetailDto.setUserRole(GroupRole.NON_ADMIN.toString());
      userDetailDto.setDob(date);
      userDetailDto.setAddress("111 Address Cary, NC");
      userDetailDto.setPassword("Secret@12346");
    }

    private void createGroup() throws Exception {
        String uri = "/groups/createGroup";
        String json = mapToJson(groupDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        assertEquals(201, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        ApiResponseDto apiResponseDto = mapFromJson(content, ApiResponseDto.class);
        assertEquals("Created Group with GroupName: 'Admin_Group'",apiResponseDto.getMessage());
        assertEquals(HttpStatus.CREATED,apiResponseDto.getHttpStatus());
    }

    private void createUser() throws Exception {
        userDetailDto.setUserName("Krishna.Kolukuluri");
        userDetailDto.setPassword("Secret@123456");
        String json = mapToJson(userDetailDto);
        String uri = "/users/createUser";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
    }

    @Test
    public void test_GetGroups() throws Exception {
        String uri = "/groups/all";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void test_CreateGroup() throws Exception {
        createGroup();
    }

    @Test
    public void test_CreateGroup_Exceptions() throws Exception {
        createGroup();
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/groups/createGroup").contentType(MediaType.APPLICATION_JSON).content(mapToJson(groupDto))).andReturn();
        assertEquals(400, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("BAD_REQUEST"));
    }

    @Test
    public void test_addGroupUsers() throws Exception {
        createGroup();
        createUser();
        String uri = "/groups/Admin_Group/users/add";
        List<String> userNames = new ArrayList<>();
        userNames.add("Krishna.Kolukuluri");
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(mapToJson(userNames))).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void test_deleteGroupUsers() throws Exception {
        createGroup();
        userDetailDto.setUserRole(GroupRole.ADMIN.toString());
        createUser();
        String uri = "/groups/Admin_Group/users/delete";
        List<String> userNames = new ArrayList<>();
        userNames.add("Krishna.Kolukuluri");
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri).contentType(MediaType.APPLICATION_JSON).content(mapToJson(userNames))).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void test_getGroupWithUsers() throws Exception {
        createGroup();
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/groups/Admin_Group").contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void test_getGroupWithUsers_NotFound() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/groups/Admin_Group").contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(404, mvcResult.getResponse().getStatus());
    }

    @Test
    public void test_UpdateGroupDetail() throws Exception {
        createGroup();
        String json = mapToJson(groupDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put("/groups/Admin_Group").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void test_DeleteGroupDetail() throws Exception {
        createGroup();
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete("/groups/Admin_Group").contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }
}
