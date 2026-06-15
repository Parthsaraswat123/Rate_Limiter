package com.example.rate_limiter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.rate_limiter.RateMiliterInterceptor;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final RateMiliterInterceptor rateMiliterInterceptor;

    public WebConfig(RateMiliterInterceptor rateMiliterInterceptor){
        this.rateMiliterInterceptor = rateMiliterInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(rateMiliterInterceptor).addPathPatterns("/api/**");
    }
}
