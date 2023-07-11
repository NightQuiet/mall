package com.hbwxz.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Night
 * @date 2023/7/10 21:51
 */
@Data
@Entity
@Table(name = "oauth_user")
public class OauthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="user_name")
    private String userName;

    @Column(name="password")
    private String password;

    @Column(name="user_role")
    private String userRole;
}
