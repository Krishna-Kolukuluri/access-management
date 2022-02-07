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
    @Column(name ="id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="user_id")
    private UserDetail userDetail;

    @ManyToOne()
    @JoinColumn(name="group_id")
    private GroupDetail groupDetail;

}
