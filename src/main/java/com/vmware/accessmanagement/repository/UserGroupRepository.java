package com.vmware.accessmanagement.repository;

import com.vmware.accessmanagement.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    @Modifying
    @Query("delete from UserGroup g where g.groupDetail.id=?1")
    int deleteByGroupID(@Param("groupid") Long groupId);

}
