package com.talkingdata.oauth2.service;

import com.talkingdata.oauth2.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.loadUserByUsername(username);
    }

    public String loadUsernameByToken(String token) throws UsernameNotFoundException {
        return userDao.loadUsernameByToken(token);
    }


}
