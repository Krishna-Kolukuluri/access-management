package com.vmware.accessmanagement.dto;

import com.vmware.accessmanagement.model.GroupDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GroupDto {
    private Long id;
    private String groupName;
    private String groupDescription;
    private String groupRole;
    private String groupPermission;

    public GroupDto(GroupDetail groupDetail){
        this.id = groupDetail.getId();
        this.groupName=groupDetail.getGroupName();
        this.groupDescription=groupDetail.getGroupDescription();
        this.groupRole=groupDetail.getGroupRole();
        this.groupPermission = groupDetail.getGroupPermission();
    }
}
