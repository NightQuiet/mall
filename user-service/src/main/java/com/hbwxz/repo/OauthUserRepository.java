package com.hbwxz.repo;

import com.hbwxz.pojo.OauthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Night
 * @date 2023/7/16 13:52
 */
@Repository
public interface OauthUserRepository extends JpaRepository<OauthUser, Integer> {

    /**
     * 根据username查询
     * @param username
     * @return
     */
    OauthUser findByUserName(String username);

    /**
     * 根据phoneNumber查询
     * @param phoneNumber
     * @return
     */
    OauthUser findByUserPhone(String phoneNumber);

}
