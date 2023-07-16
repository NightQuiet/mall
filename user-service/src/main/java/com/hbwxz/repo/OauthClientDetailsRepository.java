package com.hbwxz.repo;

import com.hbwxz.pojo.OauthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Night
 * @date 2023/7/16 13:52
 */
@Repository
public interface OauthClientDetailsRepository extends JpaRepository<OauthClientDetails, Integer> {

    /**
     * 根据clientId查询
     * @param clientId
     * @return
     */
    OauthClientDetails findByClientId(String clientId);

    /**
     * 对于手机+验证码登录，本身没有密码的，code（验证码）就是它的密码，所以这个密码需要随时进行更替
     * 用户登录30天token有效，30天以后需要重新登录，如果30天后使用phone +code就需要更新
     * OauthUser表的password字段和oOauthClientDetails表的client_secret字段为当前code。
     * OauthUser表可更新也可以不更新，这里建议同步更新，方便后期维护
     * @param secret
     * @param clientId
     */
    @Query(value = "update oauth_client_details set client_secret = ?1 where client_id = ?2", nativeQuery = true)
    @Modifying
    @Transactional
    void updateScopeByClientId(String secret, String clientId);
}
