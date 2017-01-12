package com.talkingdata.oauth2.filter;


import com.talkingdata.oauth2.dao.UserDao;
import com.talkingdata.oauth2.utils.RateLimiter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Setter
public class RateLimitFilter implements Filter {

    private RateLimiter rateLimiter;
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
            if (!rateLimiter.access(username)) {
                throw new AccessDeniedException("The times of usage is limited");
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw new AccessDeniedException(e.getMessage(), e);
        }

    }

    @Override
    public void destroy() {

    }

}
