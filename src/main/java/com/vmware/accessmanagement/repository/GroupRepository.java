package com.vmware.accessmanagement.repository;

import com.vmware.accessmanagement.dto.GroupDto;
import com.vmware.accessmanagement.model.GroupDetail;
import com.vmware.accessmanagement.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupDetail, Long> {
    GroupDetail findGroupDetailByGroupName(String groupName);
}
