package com.vmware.accessmanagement.repository;

import com.vmware.accessmanagement.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    int deleteById(@Param("user_group_id") int userGroupId);
}
