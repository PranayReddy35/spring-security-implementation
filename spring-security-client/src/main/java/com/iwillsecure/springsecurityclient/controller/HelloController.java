package com.iwillsecure.springsecurityclient.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(){
        return "Hello, Welcome to this Demo Application " +
                "with Spring Security. "+
                "Hopefully this application will serve as " +
                "a reference for the future.";
    }
}
