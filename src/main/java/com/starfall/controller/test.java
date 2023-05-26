package com.starfall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class test {
    @RequestMapping("/test")
    public String test1(){
        return "index";
    }


    @RequestMapping("/test2")
    public String test2(
            @RequestParam(required = false,defaultValue = "kong")String user
    ){
        System.out.println(user);
        return "test";
    }


    @RequestMapping("/test3")
    public String test3(
            @CookieValue(value = "CookieId",defaultValue = "missing")
            String cookieValue,
            HttpSession session

    ){
        System.out.println("cookieValue = " + cookieValue);
        return "test";
    }


    @RequestMapping("/test4")
    public String test4(
            @RequestParam()String user,
            HttpSession session
    ){
        session.setAttribute("user_input",user);
        return "test";
    }
}
