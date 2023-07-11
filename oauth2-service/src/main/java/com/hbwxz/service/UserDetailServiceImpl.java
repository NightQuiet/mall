package com.hbwxz.service;

import com.hbwxz.pojo.OauthUser;
import com.hbwxz.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Night
 * @date 2023/7/10 22:01
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        OauthUser user = userRepository.queryByUserName(username);
        if(user!=null){
            return new User(user.getUserName(),user.getPassword(),
                            AuthorityUtils.createAuthorityList(user.getPassword()));
        }else {
            throw new UsernameNotFoundException("用户["+username+"]不存在");
        }
    }
}
