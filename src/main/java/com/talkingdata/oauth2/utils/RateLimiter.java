package com.talkingdata.oauth2.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

@Component
public class RateLimiter {

    /**
     * 一段时间
     */
    @Value("${access.limit.intervalInMills}")
    private long intervalInMills;
    /**
     * 桶的最多令牌数
     */
    @Value("${access.limit.limit}")
    private long limit;
    /**
     * 每隔多长时间放一个令牌
     */
    private double intervalPerPermit;

    private JedisPool jedisPool;

    public RateLimiter() {
        jedisPool = JedisPoolProvider.getJedisPool();
        intervalPerPermit = intervalInMills * 1.0 / limit;
    }

    public synchronized boolean access(String userId) {

        String key = genKey(userId);

        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> counter = jedis.hgetAll(key);

            if (counter.size() == 0) {
                TokenBucket tokenBucket = new TokenBucket(System.currentTimeMillis(), limit - 1);
                jedis.hmset(key, tokenBucket.toHash());
                return true;
            } else {
                TokenBucket tokenBucket = TokenBucket.fromHash(counter);

                long lastRefillTime = tokenBucket.getLastRefillTime();
                long refillTime = System.currentTimeMillis();
                long intervalSinceLast = refillTime - lastRefillTime;

                long currentTokensRemaining;
                if (intervalSinceLast > intervalInMills) {
                    currentTokensRemaining = limit;
                } else {
                    long grantedTokens = (long) (intervalSinceLast / intervalPerPermit);
                    System.out.println("grantedTokens=" + grantedTokens);
                    currentTokensRemaining = Math.min(grantedTokens + tokenBucket.getTokensRemaining(), limit);
                }
                assert currentTokensRemaining >= 0;
                if (currentTokensRemaining == 0) {
                    tokenBucket.setTokensRemaining(currentTokensRemaining);
                    jedis.hmset(key, tokenBucket.toHash());
                    System.out.println("tokenBucket=" + tokenBucket);
                    return false;
                } else {
                    tokenBucket.setLastRefillTime(refillTime);
                    tokenBucket.setTokensRemaining(currentTokensRemaining - 1);
                    jedis.hmset(key, tokenBucket.toHash());
                    System.out.println("tokenBucket=" + tokenBucket);
                    return true;
                }

            }
        }
    }

    private String genKey(String userId) {
        return "rate:limiter:" + intervalInMills + ":" + limit + ":" + userId;
    }

    @ToString
    @Getter
    @Setter
    public static class TokenBucket {
        private long lastRefillTime;
        private long tokensRemaining;

        public TokenBucket(long lastRefillTime, long tokensRemaining) {
            this.lastRefillTime = lastRefillTime;
            this.tokensRemaining = tokensRemaining;
        }

        public static TokenBucket fromHash(Map<String, String> hash) {
            long lastRefillTime = Long.parseLong(hash.get("lastRefillTime"));
            int tokensRemaining = Integer.parseInt(hash.get("tokensRemaining"));
            return new TokenBucket(lastRefillTime, tokensRemaining);
        }

        public Map<String, String> toHash() {
            Map<String, String> hash = new HashMap<>();
            hash.put("lastRefillTime", String.valueOf(lastRefillTime));
            hash.put("tokensRemaining", String.valueOf(tokensRemaining));
            return hash;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RateLimiter rateLimiter = new RateLimiter();

        for (int i = 0; i < 3; i++) {
            boolean root = rateLimiter.access("root");
            System.out.println(root);
        }

        boolean root = rateLimiter.access("root");
        System.out.println(root);

        Thread.sleep(4000);
        root = rateLimiter.access("root");
        System.out.println(root);

    }
}
