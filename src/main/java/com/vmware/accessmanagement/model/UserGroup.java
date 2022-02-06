package com.vmware.accessmanagement.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name ="UserGroup")
@NoArgsConstructor
@Getter
@Setter
public class UserGroup {
    @Id
    @Column(name ="user_group_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;

    @Column(name ="user_id")
    Long userId;

    @Column(name ="group_id")
    Long groupId;

    @ManyToOne
    @JoinColumn(name="userDetail_id")
    private UserDetail userDetail;

    @ManyToOne
    @JoinColumn(name="groupDetail_id")
    private UserDetail groupDetail;

}
