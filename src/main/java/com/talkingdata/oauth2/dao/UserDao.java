package com.talkingdata.oauth2.dao;

import com.talkingdata.oauth2.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UserDetails loadUserByUsername(String username) {
        List<UserDetails> list = jdbcTemplate.query("select username,password,enabled from users where username = ?", new String[]{username},
                new RowMapper<UserDetails>() {
                    public UserDetails mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        String username = rs.getString(1);
                        String password = rs.getString(2);
                        boolean enabled = rs.getBoolean(3);
                        return new User(username, password, enabled);
                    }

                });
        return list.get(0);
    }

    public String loadUsernameByToken(String token) {
        return jdbcTemplate.queryForObject("select user_name from oauth_access_token where token_id = ?", new String[]{extractTokenKey(token)}, String.class);
    }

    /**
     * @param value
     * @return
     * @see org.springframework.security.oauth2.provider.token.store.JdbcTokenStore#extractTokenKey
     */
    protected String extractTokenKey(String value) {
        if (value == null) {
            return null;
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
        }

        try {
            byte[] bytes = digest.digest(value.getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, bytes));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
        }
    }

}
