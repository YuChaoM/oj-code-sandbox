package com.yuchao.ojcodesandbox.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 蒙宇潮
 * @create 2023-09-09  17:30
 */

@RestController("/")
public class MainController {

    @GetMapping("health")
    public String checkHealth() {
        return "ok";
    }
}
