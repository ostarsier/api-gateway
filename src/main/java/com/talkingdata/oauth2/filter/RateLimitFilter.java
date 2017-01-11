package com.talkingdata.oauth2.filter;


import com.talkingdata.oauth2.dao.UserDao;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class RateLimitFilter extends HttpServlet implements Filter {

    private UserDao userDao;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String auth = httpRequest.getHeader("Authorization");
            String accessToken = auth.split(" ")[1];
            String username = userDao.loadUsernameByToken(accessToken);
            System.out.println("username=" + username);
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw new AccessDeniedException("The times of usage is limited");
        }

    }

    @Override
    public void destroy() {

    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
