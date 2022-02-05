package com.vmware.accessmanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vmware.accessmanagement.encryption.AttributeEncryptor;
import com.vmware.accessmanagement.validator.ValidPassword;
import com.vmware.accessmanagement.validator.ValidUserName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name ="User")
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @Column(name ="id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "UserName is mandatory")
    @ValidUserName
    //@Size(min=5, max=20)
    @Column(name = "user_name",unique = true)
    String userName;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "dob")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    Date dob;

    @Column(name = "is_admin")
    Boolean isAdmin;

    @Column(name = "address")
    String address;

    @Convert(converter = AttributeEncryptor.class)
    @NotBlank(message = "Password is mandatory")
    @ValidPassword
    @Column(name = "password")
    String password;

    public User(String firstName, String lastName, String userName, Date dob, Boolean isAdmin,String address,String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.dob = dob;
        this.isAdmin=isAdmin;
        this.address=address;
        this.password =password;
    }
}
