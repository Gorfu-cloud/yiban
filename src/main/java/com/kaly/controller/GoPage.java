package com.kaly.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by 国瑚 on 2020/7/13.
 */
@Controller
public class GoPage {
    @GetMapping("/")
    public String goLogin(){
        return "index";
    }

    @GetMapping("/gdpu")
    public String goLogin1(){
        return "index";
    }

    @GetMapping("/goInfo")
    public String goInfo(){
        return "info";
    }
}
