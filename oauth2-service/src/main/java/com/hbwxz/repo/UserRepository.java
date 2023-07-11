package com.hbwxz.repo;

import com.hbwxz.pojo.OauthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Night
 * @date 2023/7/10 21:59
 */
@Repository
public interface UserRepository extends JpaRepository<OauthUser,Integer> {

    OauthUser queryByUserName(String userName);
}
