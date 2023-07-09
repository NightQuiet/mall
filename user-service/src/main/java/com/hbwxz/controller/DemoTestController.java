package com.hbwxz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Night
 * @date 2023/7/9 22:05
 */
@RestController
public class DemoTestController {
    @GetMapping("/demo")
    public String demo(){
        return "demo";
    }
}
