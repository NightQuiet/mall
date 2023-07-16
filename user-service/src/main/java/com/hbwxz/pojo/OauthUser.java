package com.hbwxz.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Night
 * @date 2023/7/10 21:51
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "user_phone")
    private String userPhone;

    @Column(name = "user_province")
    private String userProvince;

    @Column(name = "vip_status")
    private int vipStatus;

    @Column(name = "vip_epoch")
    private int vipEpoch;

    @Column(name = "vip_buy_date")
    private Date vipBuyDate;

    @Column(name = "vip_end_date")
    private Date vipEndDate;
}
