package com.vmware.accessmanagement.dao;

import com.vmware.accessmanagement.dto.UserDto;
import com.vmware.accessmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUserName(@Param("userName") String userName);
    List<User> findAll();
    boolean existsUserByUserName(@Param("userName") String userName);
    boolean existsByUserName(String userName);
}
