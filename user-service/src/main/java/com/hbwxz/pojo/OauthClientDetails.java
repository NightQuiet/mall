package com.hbwxz.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author Night
 * @date 2023/7/10 21:51
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "oauth_client_details")
public class OauthClientDetails {
    @Id
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "resource_ids")
    private String resourceIds;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "scope")
    private String scope;

    @Column(name = "authorized_grant_types")
    private String authorizedGrantTypes;

    @Column(name = "web_server_redirect_uri")
    private String redirectUrl;

    @Column(name = "authorities")
    private String authorities;

    @Column(name = "access_token_validity")
    private Integer accessTokenValidity;

    @Column(name = "refresh_token_validity")
    private Integer refreshTokenValidity;

    @Column(name = "additional_information")
    private String additionalInfo;

    @Column(name = "autoapprove")
    private String autoApprove;
}