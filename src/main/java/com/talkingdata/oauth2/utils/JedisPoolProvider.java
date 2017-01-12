package com.talkingdata.oauth2.utils;

import redis.clients.jedis.JedisPool;

import java.util.Properties;

public class JedisPoolProvider {

    private static JedisPool jedisPool;

    static {
        try {
            Properties props = new Properties();
            props.load(JedisPoolProvider.class.getClassLoader().getResourceAsStream("redis.properties"));
            jedisPool = new JedisPool(props.getProperty("host"), Integer.valueOf(props.getProperty("port")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static JedisPool getJedisPool() {
        return jedisPool;
    }


}
