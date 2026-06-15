package com.example.rate_limiter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimiterFallbackTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private RateLimiterFixedWindow rateLimiterFixedWindow;

    @InjectMocks
    private RateLimiterService rateLimiterService;

    @Test
    void testIsAllowed_whenRedisSucceeds() {
        // Arrange
        String key = "test-ip";
        String redisKey = "ratelimit:" + key;
        
        when(redisTemplate.execute(
                any(RedisScript.class),
                eq(Collections.singletonList(redisKey)),
                any(Object[].class)
        )).thenReturn(1L);

        // Act
        boolean allowed = rateLimiterService.isAllowed(key);

        // Assert
        assertTrue(allowed);
        verifyNoInteractions(rateLimiterFixedWindow);
    }

    @Test
    void testIsAllowed_whenRedisFails_fallsBackToFixedWindow() {
        // Arrange
        String key = "test-ip";
        String redisKey = "ratelimit:" + key;

        when(redisTemplate.execute(
                any(RedisScript.class),
                eq(Collections.singletonList(redisKey)),
                any(Object[].class)
        )).thenThrow(new RedisConnectionFailureException("Redis is down"));

        when(rateLimiterFixedWindow.isAllowed(key)).thenReturn(true);

        // Act
        boolean allowed = rateLimiterService.isAllowed(key);

        // Assert
        assertTrue(allowed);
        verify(rateLimiterFixedWindow, times(1)).isAllowed(key);
    }
}
