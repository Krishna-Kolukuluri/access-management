package com.vmware.accessmanagement.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

    @Column(name ="group_name")
    String groupName;

    @Column(name ="group_description")
    String groupDescription;

    @Column(name ="group_role")
    String groupRole;

    @Column(name ="group_permission")
    String groupPermission;

    @OneToMany(mappedBy = "groupDetail")
    List<UserGroup> userGroups;

    public GroupDetail(String groupName, String groupDescription, String groupRole, String groupPermission) {
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupRole=groupRole;
        this.groupPermission = groupPermission;
    }
}
