package com.vmware.accessmanagement.repository;

import com.vmware.accessmanagement.dto.GroupDto;
import com.vmware.accessmanagement.model.GroupDetail;
import com.vmware.accessmanagement.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<GroupDetail, Long> {
    GroupDetail findGroupDetailByGroupName(String groupName);
    List<GroupDetail> findAll();
    int deleteByGroupName(@Param("groupName") String groupName);
}
