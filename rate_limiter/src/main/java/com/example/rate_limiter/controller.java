package com.example.rate_limiter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// import com.example.rate_limiter.RateLimiterManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api") 
public class controller {

    // @Autowired
    // private RateLimiterManagerService rateLimiterManagerService;
    
    // @GetMapping("/access")
    // public ResponseEntity<Sting> access(@RequestHeader(value = "X-Forwarded-For", defaultValue = "127.0.0.1") String ip){

    //     // if(rateLimiterManagerService.allowAccess(ip)){
    //     //     return ResponseEntity.ok("Access granted");
    //     // }
    //     // else{
    //     //     return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Access denied !");
    //     // }

    // }

    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }
    

}
