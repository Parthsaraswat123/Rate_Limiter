package com.example.rate_limiter;



public class TokenBucket {
    private int bucketCapacity;
    private int tokens;
    private int fillRate;
    private long refillTimestamp;

    public TokenBucket(int bucketCapacity,int capacity, int fillRate){
        this.bucketCapacity = bucketCapacity;
        this.tokens = bucketCapacity;
        this.fillRate = fillRate;
        this.refillTimestamp = System.currentTimeMillis();
    }

    private void rateRefiller() {
        long now = System.currentTimeMillis();
        
        long elapsed = now - refillTimestamp;

        if(elapsed > 0){
            long tokensToAdd = (elapsed * fillRate);
            bucketCapacity = Math.min(tokens, bucketCapacity + (int)tokensToAdd);
            refillTimestamp = now;
        }
    }

    public boolean tryConsume(int token){
        rateRefiller();

        if(bucketCapacity >= token){
            bucketCapacity -= token;
            return true;
        }
        else{
            return false;
        }
    }

}
