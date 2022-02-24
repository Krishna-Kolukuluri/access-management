package com.vmware.accessmanagement.controller;

import com.vmware.accessmanagement.dto.ApiResponseDto;
import com.vmware.accessmanagement.dto.GroupDetailDto;
import com.vmware.accessmanagement.dto.UserDetailDto;
import com.vmware.accessmanagement.dto.UserViewDto;
import com.vmware.accessmanagement.exception.ApiError;
import com.vmware.accessmanagement.model.GroupPermission;
import com.vmware.accessmanagement.model.GroupRole;
import com.vmware.accessmanagement.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Log4j2
@Transactional
public class UserControllerTest extends BaseTest {
    private static UserDetailDto userDetailDto;
    static List<GroupDetailDto> groups = new ArrayList<>();
    static GroupDetailDto groupDto = new GroupDetailDto();

    @Mock
    private UserService userService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity()).build();

        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public static void setUser() throws ParseException {
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

        groupDto.setGroupName("Non_Admin_Group");
        groupDto.setGroupRole(GroupRole.NON_ADMIN.toString());
        groupDto.setGroupPermission(GroupPermission.READ.toString());
        groupDto.setGroupDescription("Testing");
    }
    private void createUser() throws Exception {
        userDetailDto.setUserName("Krishna.Kolukuluri");
        userDetailDto.setPassword("Secret@123456");
        String json = mapToJson(userDetailDto);
        String uri = "/users/createUser";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        assertEquals(201, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        ApiResponseDto apiResponseDto = mapFromJson(content, ApiResponseDto.class);
        assertEquals("Created User with UserName: '"+ userDetailDto.getUserName()+"'", apiResponseDto.getMessage());
    }

    private void createGroup() throws Exception {
        String uri = "/groups/createGroup";
        String json = mapToJson(groupDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_InternalServerException() throws Exception {
        userDetailDto.setPassword("Secret@12346");
        String json = mapToJson(userDetailDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put("/users/Krishna.Kolukuluri").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        assertEquals(500, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        ApiError apiResponseDto = mapFromJson(content, ApiError.class);
        assertEquals("User not found ::Krishna.Kolukuluri", apiResponseDto.getMessage());
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_ConstraintViolationException() throws Exception {
        userDetailDto.setPassword("Secret");
        String json = mapToJson(userDetailDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/users/createUser").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        log.info(mvcResult.getResponse().getStatus()+"Krishna_mvcResult.getResponse().getStatus()");
        assertEquals(400, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        ApiError apiResponseDto = mapFromJson(content, ApiError.class);
        assertEquals("Data Validation Error", apiResponseDto.getMessage());
        assertEquals("password: Invalid Password", apiResponseDto.getErrors().get(0));
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_GetUsers() throws Exception {
        String uri = "/users/all";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_GetUser() throws Exception {
        createUser();
        String uri = "/users/Krishna.Kolukuluri";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        UserViewDto apiResponseDto = mapFromJson(content, UserViewDto.class);
        assertEquals(userDetailDto.getUserName(), apiResponseDto.getUserName());
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_GetUserWithGroups_NotFound() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/users/abcGroup").contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(404, mvcResult.getResponse().getStatus());
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_deleteUserGroups() throws Exception {
        createUser();
        createGroup();
        GroupDetailDto group = new GroupDetailDto();
        group.setGroupDescription(groupDto.getGroupDescription());
        group.setGroupRole(groupDto.getGroupRole());
        group.setGroupPermission(groupDto.getGroupPermission());
        group.setGroupName(groupDto.getGroupName());
        groups.add(group);
        String json = mapToJson(groups);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete("/users/Krishna.Kolukuluri/groups/delete").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_addUserGroups() throws Exception {
        createUser();
        createGroup();
        GroupDetailDto group = new GroupDetailDto();
        group.setGroupDescription(groupDto.getGroupDescription());
        group.setGroupRole(groupDto.getGroupRole());
        group.setGroupPermission(groupDto.getGroupPermission());
        group.setGroupName(groupDto.getGroupName());
        groups.add(group);
        String json = mapToJson(groups);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/users/Krishna.Kolukuluri/groups/add").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_UpdateUserDetail() throws Exception {
        createUser();
        String json = mapToJson(userDetailDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put("/users/Krishna.Kolukuluri").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_MediaNotSupportedException() throws Exception {
        String json = mapToJson(userDetailDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put("/users/Krishna.Kolukuluri").contentType(MediaType.APPLICATION_ATOM_XML).content(json)).andReturn();
        assertEquals(415, mvcResult.getResponse().getStatus());
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_DeleteUserDetail() throws Exception {
        createUser();
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete("/users/Krishna.Kolukuluri").contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_MethodNotAllowedException() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch("/users/Krishna.Kolukuluri/groups/add").contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(405, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        ApiError apiResponseDto = mapFromJson(content, ApiError.class);
        assertEquals("Request method 'PATCH' not supported", apiResponseDto.getMessage());
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_CreateUser() throws Exception {
        createUser();
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_CreateUser_Exceptions() throws Exception {
        createUser();
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/users/createUser").contentType(MediaType.APPLICATION_JSON).content( mapToJson(userDetailDto))).andReturn();
        assertEquals(400, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("BAD_REQUEST"));
    }



    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_CreateUserWithInValidUserName() throws Exception {
        userDetailDto.setUserName("Kris");
        userDetailDto.setPassword("Secret@123456");
        String json = mapToJson(userDetailDto);
        String uri = "/users/createUser";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        assertEquals(400, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        ApiError errors = mapFromJson(content, ApiError.class);
        assertEquals( "Data Validation Error", errors.getMessage());
        assertEquals(1, errors.getErrors().size());
        assertEquals("userName: Invalid User Name", errors.getErrors().get(0));
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_CreateUserWithInValidPassword() throws Exception {
        userDetailDto.setPassword("Secretabcde");
        userDetailDto.setUserName("Krishna.Kolukuluri");
        String json = mapToJson(userDetailDto);
        String uri = "/users/createUser";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        assertEquals(400, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        ApiError errors = mapFromJson(content, ApiError.class);
        assertEquals( "Data Validation Error", errors.getMessage());
        assertEquals(1, errors.getErrors().size());
        assertEquals("password: Invalid Password", errors.getErrors().get(0));
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_CreateUserWithInValidPasswordAndUsername() throws Exception {
        userDetailDto.setUserName("Kris");
        userDetailDto.setPassword("Secretabcde");
        String json = mapToJson(userDetailDto);
        String uri = "/users/createUser";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        assertEquals(400, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        ApiError errors = mapFromJson(content, ApiError.class);
        assertEquals( "Data Validation Error", errors.getMessage());
        assertEquals(2, errors.getErrors().size());
    }

    @Test
    @WithMockUser(username="Krishna", password = "password", roles={"user", "admin"})
    public void test_GetUsers_LogLevel() throws Exception {
        String uri = "/users/all?logLevel=info";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        uri = "/users/all?logLevel=debug";
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        uri = "/users/all?logLevel=error";
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        uri = "/users/all?logLevel=trace";
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        uri = "/users/all?logLevel=warn";
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        uri = "/users/all?logLevel=off";
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        uri = "/users/all?logLevel=fatal";
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        uri = "/users/all?logLevel=all";
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }
}
