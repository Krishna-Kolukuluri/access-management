package com.vmware.accessmanagement.service;

import com.vmware.accessmanagement.model.GroupRole;
import com.vmware.accessmanagement.repository.UserRepository;
import com.vmware.accessmanagement.model.UserDetail;
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
        UserDetail user = new UserDetail();
        user.setUserId(1L);
        user.setFirstName("FName");
        user.setLastName("LName");
        user.setUserName("UName");
        user.setUserRole(GroupRole.ADMIN);
        String dob="31/12/1998";
        Date date=new SimpleDateFormat("dd/MM/yyyy"). parse(dob);
        user.setDob(date);
        user.setAddress("XXXXXXX");
        when(userRepository.save(user)).thenReturn(user);
        userService.createUser(user);
        verify(userRepository, times(1)).save(any());
    }

}
