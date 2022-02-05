package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.dao.UserRepository;
import com.vmware.accessmanagement.model.User;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Log4j2
public class UserServiceImplTests {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_CreatedUser() throws ParseException {
        User user = new User();
        user.setId(1L);
        user.setFirstName("FName");
        user.setLastName("LName");
        user.setUserName("UName");
        user.setIsAdmin(true);
        String dob="31/12/1998";
        Date date=new SimpleDateFormat("dd/MM/yyyy"). parse(dob);
        user.setDob(date);
        user.setAddress("XXXXXXX");
        when(userRepository.save(user)).thenReturn(user);
        userService.createUser(user);
        verify(userRepository, times(1)).save(any());
    }

}
