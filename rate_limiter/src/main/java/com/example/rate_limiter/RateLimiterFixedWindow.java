package com.example.rate_limiter;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Service
@Slf4j
public class RateLimiterFixedWindow {

    @Value("${ratelimiter.fixedWindow.window}")
    private long WINDOW_SIZE_MS; // 60 seconds

    @Value("${ratelimiter.fixedWindow.limit}")
    private int MAX_REQUESTS; // limit per window

    private static class Window {
        final long startTime;
        final AtomicInteger count;

        Window(long startTime) {
            this.startTime = startTime;
            this.count = new AtomicInteger(0);
        }
    }

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();


    public boolean isAllowed(String key) {

        for (Map.Entry<String, Window> entry : windows.entrySet()) {
            log.info("Windows: {}", entry);
        }

        long now = System.currentTimeMillis();
        Window window = windows.compute(key, (k, existingWindow) -> {
            if (existingWindow == null || (now - existingWindow.startTime) >= WINDOW_SIZE_MS) {
                return new Window(now);
            }
            return existingWindow;
        });

        int currentCount = window.count.incrementAndGet();
        boolean allowed = currentCount <= MAX_REQUESTS;

        log.info("Fixed window check for key {}: count={}, allowed={}", key, currentCount, allowed);
        return allowed;

    }
}