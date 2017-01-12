package com.talkingdata.oauth2;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@EnableAuthorizationServer
@EnableZuulProxy
@SpringCloudApplication
public class App {

    public static void main(String[] args) {
        new SpringApplicationBuilder(App.class).web(true).run(args);
    }

}
