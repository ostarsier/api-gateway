package com.talkingdata.oauth2.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @RequestMapping(value="/test", method= RequestMethod.GET)
    public String test() {
        log.info("hello,lo4j2");
        return "hello user";
    }

}
