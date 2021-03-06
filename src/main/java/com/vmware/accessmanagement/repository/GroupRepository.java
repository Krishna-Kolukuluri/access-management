package com.vmware.accessmanagement.repository;

import com.vmware.accessmanagement.model.GroupDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<GroupDetail, Long> {
    GroupDetail findGroupDetailByGroupName(String groupName);
    List<GroupDetail> findAll();
    Page<GroupDetail> findAll(Pageable pageable);
    int deleteByGroupName(@Param("groupName") String groupName);
}
