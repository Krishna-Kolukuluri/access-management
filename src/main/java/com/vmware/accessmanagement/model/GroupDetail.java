package com.vmware.accessmanagement.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name ="GroupDetail")
@NoArgsConstructor
@Getter
@Setter
public class GroupDetail {
    @Id
    @Column(name ="group_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "groupName is mandatory")
    @Column(name ="group_name", unique = true)
    String groupName;

    @Column(name ="group_description")
    String groupDescription;

    @NotBlank(message = "groupRole is mandatory")
    @Column(name ="group_role")
    String groupRole;

    @NotBlank(message = "groupPermission is mandatory")
    @Column(name ="group_permission")
    String groupPermission;

    @OneToMany(mappedBy = "groupDetail")
    List<UserGroup> users;

    public GroupDetail(String groupName, String groupDescription,
                       String groupRole, String groupPermission) {
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupRole=groupRole;
        if(groupPermission.equals(GroupPermission.ALL.toString())){
            this.groupPermission = GroupPermission.ALL.toString();
        }else if(groupPermission.equals(GroupPermission.READ.toString())){
            this.groupPermission = GroupPermission.READ.toString();
        }else if(groupPermission.equals(GroupPermission.WRITE.toString())){
            this.groupPermission  = GroupPermission.WRITE.toString();
        }else{
            this.groupPermission = GroupPermission.NONE.toString();
        }
        if(groupRole.equals(GroupRole.ADMIN.toString())){
            this.groupRole = GroupRole.ADMIN.toString();
        }else{
            this.groupRole = GroupRole.NON_ADMIN.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupDetail)) return false;
        GroupDetail that = (GroupDetail) o;
        return getGroupName().equals(that.getGroupName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGroupName());
    }
}
