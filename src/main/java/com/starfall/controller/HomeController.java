package com.starfall.controller;

import com.starfall.config.sf_config;
import com.starfall.service.DiscussService;
import com.starfall.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;


@Controller
public class HomeController{
    @RequestMapping("/home")
    public String home(
            HttpSession session,
            @RequestParam(value = "only_user",required = false) String only_user,
            @RequestParam(value = "page",required = false) String page_str
    ) {
        ApplicationContext context = new AnnotationConfigApplicationContext(sf_config.class);
        DiscussService discussService = context.getBean("discussService", DiscussService.class);
        if (Objects.equals(only_user,"null")){
            only_user = "";
        }
        session.setAttribute("only_user",only_user);
        Integer page = 1;
        session.setAttribute("page",1);
        if (!StringUtils.isEmpty(page_str)){
            page = Integer.valueOf(page_str);
        }
        Integer last_page = discussService.getPage(only_user);
        session.setAttribute("comment",discussService.getDiscuss(page,only_user));
        session.setAttribute("last_page",last_page);
        session.setAttribute("page",page);
        session.setAttribute("page_center",page+"/"+last_page);
        if(page > last_page){
            session.setAttribute("page",page-1);
        }
        else if(page <= 0){
            session.setAttribute("page",1);
        }
        return "index";
    }

    @RequestMapping("/add_comment")
    public String addComment(
            HttpSession session,
            HttpServletRequest req,
            @RequestParam(value = "content",required = false)String content,
            @RequestParam(value = "comment_VerifyCode",required = false)String code
    ) throws UnsupportedEncodingException {
        ApplicationContext context = new AnnotationConfigApplicationContext(sf_config.class);
        UserService userService = context.getBean("userService", UserService.class);
        DiscussService discussService = context.getBean("discussService", DiscussService.class);
        LocalDateTime ldt = LocalDateTime.now();
        session.setAttribute("comment","block");
        session.setAttribute("home","none");
        req.setCharacterEncoding("utf-8");
        String date = LocalDate.now()+" "+ldt.getHour()+":"+ldt.getMinute()+":"+ldt.getSecond();
        String user = (String) session.getAttribute("user");
        String name = userService.getName(user);
        session.setAttribute("comment_input",content);
        if(Objects.equals(code,session.getAttribute("code"))){
            discussService.addComment(user,content,date,name);
            discussService.updateHead();
            session.setAttribute("comment_tips","发话成功");
            session.setAttribute("comment_input",null);
            session.setAttribute("code",null);
        }
        else{
            session.setAttribute("comment_tips","验证码错误");
        }
        return "redirect:/home";
    }

    @RequestMapping("/exit")
    public String exit(
            HttpSession session
    ){
        session.invalidate();
        return "redirect:/home";
    }
}

