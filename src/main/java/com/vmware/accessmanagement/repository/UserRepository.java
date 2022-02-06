package com.vmware.accessmanagement.repository;

import com.vmware.accessmanagement.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserDetail, Long> {
    UserDetail findUserByUserName(@Param("userName") String userName);
    List<UserDetail> findAll();
    boolean existsUserByUserName(@Param("userName") String userName);
    boolean existsByUserName(String userName);
    int deleteByUserName(@Param("userName") String userName);
}
