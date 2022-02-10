package com.vmware.accessmanagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vmware.accessmanagement.dto.ApiResponseDto;
import com.vmware.accessmanagement.dto.GroupDto;
import com.vmware.accessmanagement.model.GroupPermission;
import com.vmware.accessmanagement.model.GroupRole;
import com.vmware.accessmanagement.service.GroupService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
@Transactional
//@Tag("Integration")
public class GroupControllerTest extends BaseTest{
    static List<GroupDto> groups = new ArrayList<>();
    static GroupDto groupDto = new GroupDto();
    @Mock
    private GroupService groupService;

    @BeforeEach
    public void setup() {
      MockitoAnnotations.openMocks(this);
      groupDto.setGroupName("Admin_Group");
      groupDto.setGroupRole(GroupRole.ADMIN.toString());
      groupDto.setGroupPermission(GroupPermission.ALL.toString());
      groupDto.setGroupDescription("Testing");
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
