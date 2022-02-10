package com.vmware.accessmanagement.dto;

import com.vmware.accessmanagement.model.GroupDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class GroupDetailDto extends GroupUpdateDto {
    //private Long id;
    String groupName;
    String groupRole;
    String groupPermission;

    public GroupDetailDto(GroupDetail groupDetail){
        //this.id = groupDetail.getId();
        this.groupName=groupDetail.getGroupName();
        this.groupDescription=groupDetail.getGroupDescription();
        this.groupRole=groupDetail.getGroupRole();
        this.groupPermission = groupDetail.getGroupPermission();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupDetailDto)) return false;
        GroupDetailDto groupDto = (GroupDetailDto) o;
        return getGroupName().equals(groupDto.getGroupName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGroupName());
    }
}
