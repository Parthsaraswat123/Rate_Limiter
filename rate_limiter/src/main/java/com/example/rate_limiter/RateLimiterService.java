package com.example.rate_limiter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RateLimiterService {
    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> redisScript;
    private final RateLimiterFixedWindow rateLimiterFixedWindow;

    private final String BUCKET_CAPACITY = "10";
    private final String REFILL_RATE = "2";

    public RateLimiterService(StringRedisTemplate redisTemplate, RateLimiterFixedWindow rateLimiterFixedWindow) {
        this.redisTemplate = redisTemplate;
        this.rateLimiterFixedWindow = rateLimiterFixedWindow;

        // this.redisScript = new DefaultRedisScript<>();
        // redisScript.setScriptText()

        String luaScript = """
                local key = KEYS[1]
                local bucketCapacity = tonumber(ARGV[1])
                local refillRate = tonumber(ARGV[2])
                local maxTokens = tonumber(ARGV[3])

                -- Get server time in seconds from the Redis time array
                local redisTime = redis.call('TIME')
                local now = tonumber(redisTime[1])

                -- HMGET returns an array list of values: index 1 is tokens, index 2 is lastRefillTime
                local bucket = redis.call('HMGET', key, 'tokens', 'lastRefillTime')
                local tokens = tonumber(bucket[1])
                local last_update = tonumber(bucket[2])

                if tokens == nil then
                    tokens = maxTokens
                    last_update = now
                else
                    local timePassed = now - last_update
                    local tokensToAdd = math.floor(timePassed * refillRate)

                    if tokensToAdd > 0 then
                        tokens = math.min(bucketCapacity, tokensToAdd + tokens)
                        last_update = now
                    end
                end

                if tokens > 0 then
                    tokens = tokens - 1
                    -- Save the updated data back into the hash map
                    redis.call('HMSET', key, 'tokens', tokens, 'lastRefillTime', last_update)
                    redis.call('EXPIRE', key, 60)
                    return 1
                else
                    return 0
                end
                """;

        this.redisScript = new DefaultRedisScript<>();
        this.redisScript.setScriptText(luaScript);
        this.redisScript.setResultType(Long.class);

    }

    public boolean isAllowed(String key) {

        String redisKey = "ratelimit:" + key;
        String nowInSeconds = String.valueOf(System.currentTimeMillis() / 1000);
        String bucketCapacity = BUCKET_CAPACITY;
        String refillRate = REFILL_RATE;
        String maxTokens = "10";

        try {
            Long result = redisTemplate.execute(redisScript,
                    Collections.singletonList(redisKey),
                    bucketCapacity, refillRate, maxTokens, nowInSeconds);

            return result != null && result == 1;
        } catch (Exception e) {
            System.out.println("Redis is down or returned an error. Falling back to local Fixed Window rate limiter.");
            log.error("Redis is down or returned an error. Falling back to local Fixed Window rate limiter. Error: {}", e.getMessage());
            return rateLimiterFixedWindow.isAllowed(key);
        }

    }

}

