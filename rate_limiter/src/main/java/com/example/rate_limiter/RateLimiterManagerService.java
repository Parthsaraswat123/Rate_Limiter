// package com.example.rate_limiter;

// import org.springframework.stereotype.Service;
// import java.util.concurrent.ConcurrentHashMap;
// import com.example.rate_limiter.RateLimiterApplication;
// import java.util.Map;

// @Service
// public class RateLimiterManagerService {
//     private final Map<String,TokenBucket>bucketStore = new ConcurrentHashMap<>();

//     private final int BUCKET_CAPACITY = 10;
//     private final int FILL_RATE = 2;

//     public boolean allowAccess(String user_id){
//         TokenBucket bucket = bucketStore.computeIfAbsent(user_id,key -> new TokenBucket(BUCKET_CAPACITY, BUCKET_CAPACITY, FILL_RATE));

//         return bucket.tryConsume(1);

//     }
// }


