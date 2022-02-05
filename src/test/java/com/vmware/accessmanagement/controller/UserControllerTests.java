package com.vmware.accessmanagement.controller;

import com.vmware.accessmanagement.dto.UserDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
@Transactional
public class UserControllerTests extends BaseTest {
    @Test
    public void testCreateUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setFirstName("FName");
        userDto.setLastName("LName");
        userDto.setUserName("UName");
        userDto.setIsAdmin(true);
        String dob="31-12-1998";
        Date date=new SimpleDateFormat("DD-MM-YYYY"). parse(dob);
        userDto.setDob(date);
        userDto.setAddress("XXXXXXX");
        userDto.setPassword("XYZ");
        String json = mapToJson(userDto);
        String uri = "/users/createUser";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals("Created User with User Name: '"+userDto.getUserName()+"'", content);
    }
}
