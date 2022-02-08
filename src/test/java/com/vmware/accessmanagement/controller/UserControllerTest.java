package com.vmware.accessmanagement.controller;

import com.vmware.accessmanagement.dto.ApiResponseDto;
import com.vmware.accessmanagement.dto.UserDto;
import com.vmware.accessmanagement.dto.UserViewDto;
import com.vmware.accessmanagement.exception.ApiError;
import com.vmware.accessmanagement.model.GroupRole;
import com.vmware.accessmanagement.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
@Transactional
@Tag("Integration")
public class UserControllerTest extends BaseTest {
    private static UserDto userDto;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public static void setUser() throws ParseException {
        String dob="31-12-2010";
        Date date=new SimpleDateFormat("DD-MM-YYYY"). parse(dob);
        userDto = new UserDto();
        userDto.setFirstName("Krishna");
        userDto.setLastName("Kolukuluri");
        userDto.setUserName("Krishna.Kolukuluri");
        userDto.setUserRole(GroupRole.NON_ADMIN.toString());
        userDto.setDob(date);
        userDto.setAddress("111 Address Cary, NC");
        userDto.setPassword("Secret@12346");
    }
    private void createUser() throws Exception {
        userDto.setUserName("Krishna.Kolukuluri");
        userDto.setPassword("Secret@123456");
        String json = mapToJson(userDto);
        String uri = "/users/createUser";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        assertEquals(201, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        ApiResponseDto apiResponseDto = mapFromJson(content, ApiResponseDto.class);
        assertEquals("Created User with UserName: '"+userDto.getUserName()+"'", apiResponseDto.getMessage());
    }

    @Test
    public void test_InternalServerException() throws Exception {
        userDto.setPassword("Secret@12346");
        String json = mapToJson(userDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put("/users/Krishna.Kolukuluri").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        assertEquals(500, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        ApiError apiResponseDto = mapFromJson(content, ApiError.class);
        assertEquals("User not found ::Krishna.Kolukuluri", apiResponseDto.getMessage());
    }

    @Test
    public void test_ConstraintViolationException() throws Exception {
        userDto.setPassword("Secret");
        String json = mapToJson(userDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put("/users/Krishna.Kolukuluri").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        log.info(mvcResult.getResponse().getStatus()+"Krishna_mvcResult.getResponse().getStatus()");
        assertEquals(400, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        ApiError apiResponseDto = mapFromJson(content, ApiError.class);
        assertEquals("Data Validation Error", apiResponseDto.getMessage());
        assertEquals("password: Invalid Password", apiResponseDto.getErrors().get(0));
    }

    @Test
    public void test_GetUsers() throws Exception {
        String uri = "/users/all";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void test_GetUser() throws Exception {
        createUser();
        String uri = "/users/Krishna.Kolukuluri";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        UserViewDto apiResponseDto = mapFromJson(content, UserViewDto.class);
        assertEquals(userDto.getUserName(), apiResponseDto.getUserName());
    }

    @Test
    public void test_GetUserWithGroups() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/users/abcGroup").contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void test_UpdateUserDetail() throws Exception {
        createUser();
        String json = mapToJson(userDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put("/users/Krishna.Kolukuluri").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void test_MediaNotSupportedException() throws Exception {
        String json = mapToJson(userDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put("/users/Krishna.Kolukuluri").contentType(MediaType.APPLICATION_ATOM_XML).content(json)).andReturn();
        assertEquals(415, mvcResult.getResponse().getStatus());
    }

    @Test
    public void test_DeleteUserDetail() throws Exception {
        createUser();
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete("/users/Krishna.Kolukuluri").contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void test_MethodNotAllowedException() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch("/users/Krishna.Kolukuluri").contentType(MediaType.APPLICATION_JSON)).andReturn();
        assertEquals(405, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        ApiError apiResponseDto = mapFromJson(content, ApiError.class);
        assertEquals("Request method 'PATCH' not supported", apiResponseDto.getMessage());
    }

    @Test
    public void test_CreateUser() throws Exception {
        createUser();
    }

    @Test
    public void test_CreateUser_Exceptions() throws Exception {
        createUser();
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/users/createUser").contentType(MediaType.APPLICATION_JSON).content( mapToJson(userDto))).andReturn();
        assertEquals(400, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("BAD_REQUEST"));
    }

    @Test
    public void test_CreateUserWithInValidUserName() throws Exception {
        userDto.setUserName("Kris");
        userDto.setPassword("Secret@123456");
        String json = mapToJson(userDto);
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
    public void test_CreateUserWithInValidPassword() throws Exception {
        userDto.setPassword("Secretabcde");
        userDto.setUserName("Krishna.Kolukuluri");
        String json = mapToJson(userDto);
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
    public void test_CreateUserWithInValidPasswordAndUsername() throws Exception {
        userDto.setUserName("Kris");
        userDto.setPassword("Secretabcde");
        String json = mapToJson(userDto);
        String uri = "/users/createUser";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        assertEquals(400, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        ApiError errors = mapFromJson(content, ApiError.class);
        assertEquals( "Data Validation Error", errors.getMessage());
        assertEquals(2, errors.getErrors().size());
    }

    @Test
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
