package com.house.item.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "USERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNo;

    @Column(unique = true, nullable = false)
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    @Column(nullable = false)
    private String username;

    private String photoName;

    public void changePasswordAndSalt(String newPassword, String newSalt) {
        password = newPassword;
        salt = newSalt;
    }

    public void updateUserInfo(String username, String photoName) {
        this.username = username;
        this.photoName = photoName;
    }
}

