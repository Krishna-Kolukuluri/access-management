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

    @OneToMany(orphanRemoval = true, mappedBy = "groupDetail")
    List<UserGroup> users;
}
