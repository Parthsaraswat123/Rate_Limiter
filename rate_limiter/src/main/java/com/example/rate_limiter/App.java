package com.example.rate_limiter;


public class App {

    public static void main(String[] args) {
        TokenBucket tokenBucket = new TokenBucket(10, 10, 1);

        for(int i = 1; i <= 10; i++){
            if(tokenBucket.tryConsume(1)){
                System.out.println("Request " + i + " is allowed");
            }
            else{
                System.out.println("Request " + i + " is denied");
            }
        }
    }
}
