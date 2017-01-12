package com.talkingdata.oauth2.filter;


import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.talkingdata.oauth2.dao.UserDao;
import com.talkingdata.oauth2.utils.RateLimiter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Setter
public class AccessFilter extends ZuulFilter {

    private RateLimiter rateLimiter;
    private UserDao userDao;


    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String auth = request.getHeader("Authorization");
        String accessToken = auth.split(" ")[1];
        String username = userDao.loadUsernameByToken(accessToken);
        if (!rateLimiter.access(username)) {
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            ctx.setResponseBody("The times of usage is limited");
        }
        return null;
    }
}
