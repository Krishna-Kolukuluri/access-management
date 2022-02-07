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

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="userDetail_id")
    private UserDetail userDetail;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="groupDetail_id")
    private GroupDetail groupDetail;

}
