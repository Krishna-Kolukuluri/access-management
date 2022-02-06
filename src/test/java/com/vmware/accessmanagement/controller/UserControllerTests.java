package com.vmware.accessmanagement.controller;

import com.vmware.accessmanagement.dto.UserDto;
import com.vmware.accessmanagement.exception.ApiError;
import com.vmware.accessmanagement.model.GroupRole;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
@Transactional
public class UserControllerTests extends BaseTest {
    private static UserDto userDto;

    @BeforeAll
    public static void setup() throws ParseException {
        String dob="31-12-2010";
        Date date=new SimpleDateFormat("DD-MM-YYYY"). parse(dob);
        userDto = new UserDto();
        userDto.setFirstName("Krishna");
        userDto.setLastName("Kolukuluri");
        userDto.setUserName("Krishna.Kolukuluri");
        userDto.setUserRole(GroupRole.ADMIN.toString());
        userDto.setDob(date);
        userDto.setAddress("410 Windy Peak Loop, Cary, NC");
        userDto.setPassword("Secret@123456");
    }
    @Test
    public void testCreateUserWithValidUserNameAndPassword() throws Exception {
        userDto.setUserName("Krishna.Kolukuluri");
        userDto.setPassword("Secret@123456");
        String json = mapToJson(userDto);
        String uri = "/users/createUser";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals("Created User with User Name: '"+userDto.getUserName()+"'", content);
    }

    @Test
    public void testCreateUserWithInValidUserName() throws Exception {
        userDto.setUserName("Kris");
        userDto.setPassword("Secret@123456");
        String json = mapToJson(userDto);
        String uri = "/users/createUser";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        String content = mvcResult.getResponse().getContentAsString();
        ApiError errors = mapFromJson(content, ApiError.class);
        assertEquals( "Data Validation Constraints Exception", errors.getMessage());
        assertEquals(1, errors.getErrors().size());
        assertEquals("userName: Invalid User Name", errors.getErrors().get(0));
    }

    @Test
    public void testCreateUserWithInValidPassword() throws Exception {
        userDto.setPassword("Secretabcde");
        userDto.setUserName("Krishna.Kolukuluri");
        String json = mapToJson(userDto);
        String uri = "/users/createUser";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        String content = mvcResult.getResponse().getContentAsString();
        ApiError errors = mapFromJson(content, ApiError.class);
        assertEquals( "Data Validation Constraints Exception", errors.getMessage());
        assertEquals(1, errors.getErrors().size());
        assertEquals("password: Invalid Password", errors.getErrors().get(0));
    }

    @Test
    public void testCreateUserWithInValidPasswordAndInvalidUsername() throws Exception {
        userDto.setUserName("Kris");
        userDto.setPassword("Secretabcde");
        String json = mapToJson(userDto);
        String uri = "/users/createUser";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        String content = mvcResult.getResponse().getContentAsString();
        ApiError errors = mapFromJson(content, ApiError.class);
        assertEquals( "Data Validation Constraints Exception", errors.getMessage());
        assertEquals(2, errors.getErrors().size());
    }
}
