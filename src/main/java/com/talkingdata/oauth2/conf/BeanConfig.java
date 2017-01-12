package com.talkingdata.oauth2.conf;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.talkingdata.oauth2.dao.UserDao;
import com.talkingdata.oauth2.filter.AccessFilter;
import com.talkingdata.oauth2.utils.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
public class BeanConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RateLimiter rateLimiter;

    @Bean
    public JdbcTemplate jdbcTemplate() throws Exception {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DataSource dataSource() throws Exception {
        Properties props = new Properties();
        props.load(this.getClass().getClassLoader().getResourceAsStream("db.properties"));
        try {
            return DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            throw e;
        }
    }

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        AccessFilter rateLimitFilter = new AccessFilter();
        rateLimitFilter.setUserDao(userDao);
        rateLimitFilter.setRateLimiter(rateLimiter);
        registrationBean.setFilter(rateLimitFilter);
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/api/*");
        registrationBean.setUrlPatterns(urlPatterns);
        return registrationBean;
    }

}