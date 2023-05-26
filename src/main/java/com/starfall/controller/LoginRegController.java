package com.starfall.controller;

import com.starfall.Util.GetCode;
import com.starfall.Util.MailUtil;
import com.starfall.config.sf_config;
import com.starfall.service.UserService;
import org.apache.commons.mail.EmailException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Objects;

@Controller
public class LoginRegController {

    @RequestMapping("/login")
    public String toLogin(
            HttpSession session
    ){
        session.setAttribute("reg_notice",null);
        session.setAttribute("enter_flag",false);
        return "login";
    }
    @RequestMapping("/reg")
    public String toReg(
            HttpSession session
    ){
        session.setAttribute("submit","button");
        return "reg";
    }






    @RequestMapping("/confirm_login")
    public String login(
            HttpSession session,
            @RequestParam(value = "user",required = false)String user,
            @RequestParam(value = "password",required = false)String password,
            @RequestParam(value = "login_code",required = false)String code
    ){
        ApplicationContext context = new AnnotationConfigApplicationContext(sf_config.class);
        UserService userService = context.getBean("userService", UserService.class);
//        HttpSession session = req.getSession();
//        String user = req.getParameter("user");
//        String password = req.getParameter("password");
//        String code = req.getParameter("login_code");
        session.setAttribute("user",user);
        String flag = userService.login(user);
        session.setAttribute("tips"," ");
        if(Objects.equals(code, "")){
            session.setAttribute("tips","验证码不能为空");

        }
        else if(!Objects.equals(code,session.getAttribute("code"))){
            session.setAttribute("tips","验证码错误");

        }
        else if(Objects.equals(flag, "null")){
            session.setAttribute("tips","用户名不存在");

        }
        else if(!Objects.equals(flag, password)){
            session.setAttribute("tips","密码错误!");

        }
        else if(Objects.equals(flag, password) && Objects.equals(code,session.getAttribute("code"))){
            String name = userService.getName(user);
            session.setAttribute("user",user);
            session.setAttribute("head",userService.getHead(user));
            session.setAttribute("password",password);
            session.setAttribute("login","1");
            session.setAttribute("name",name);
            session.setAttribute("code",null);
            return "redirect:/home";
        }
        return "redirect:/login";
    }






    @RequestMapping("/verify_code")
    public String reg_email(
            HttpSession session,
            @RequestParam(value = "vreg_user",required = false)String user,
            @RequestParam(value = "vreg_password",required = false)String password,
            @RequestParam(value = "vreg_email",required = false)String email,
            @RequestParam(value = "vreg_code",required = false)String code
    ){
        session.setAttribute("code","1");
        ApplicationContext context = new AnnotationConfigApplicationContext(sf_config.class);
        UserService userService = context.getBean("userService", UserService.class);
//        HttpSession session = req.getSession();
        GetCode getCode = new GetCode();
        MailUtil mail = new MailUtil();
        String email_code = "";
//        String user = req.getParameter("vreg_user");
//        String password = req.getParameter("vreg_password");
//        String email = req.getParameter("vreg_email");
//        String code = req.getParameter("vreg_code");
        session.setAttribute("reg_user",user);
        session.setAttribute("reg_password",password);
        session.setAttribute("reg_email",email);
        session.setAttribute("code_tips"," ");
        session.setAttribute("reg_notice",null);
        boolean user_flag = userService.checkUserRepeat(user);
        boolean flag = userService.checkEmailRepeat(email);
        if ((boolean) session.getAttribute("enter_flag")){
            email_code = getCode.getcode();
            session.setAttribute("email_code",email_code);
            try {
                mail.reg_mail(email,email_code);
            } catch (EmailException e) {
                e.printStackTrace();
            }
            session.setAttribute("code_tips","邮箱已重新发送，请注意查收");
            return "reg_emailCode";
        }
        else{
            if(user.length() < 3){
                session.setAttribute("reg_tips","用户名不能小于3个字符");
            }
            else if(password.length() < 6){
                session.setAttribute("reg_tips","密码不能小于6个字符");
            }
            else if (!Objects.equals(code,session.getAttribute("code"))){
                session.setAttribute("reg_tips","验证码错误");
            }
            else if(user_flag){
                session.setAttribute("reg_tips","用户已存在");
            }
            else if(flag){
                session.setAttribute("reg_tips","邮箱已存在");
            }
            else if(!flag && !user_flag && Objects.equals(code,session.getAttribute("code"))){
                session.setAttribute("enter_flag",true);
                email_code = getCode.getcode();
                session.setAttribute("email_code",email_code);
                try {
                    mail.reg_mail(email,email_code);
                } catch (EmailException e) {
                    e.printStackTrace();
                }
                session.setAttribute("code",null);
                return "reg_emailCode";
            }
        }

        return "reg";
    }




    @RequestMapping("/confirm_reg")
    public String reg(
            HttpSession session,
            @RequestParam(value = "reg_email_code",required = false)String email_code
    ){
        ApplicationContext context = new AnnotationConfigApplicationContext(sf_config.class);
        UserService userService = context.getBean("userService", UserService.class);
//        HttpSession session = req.getSession();
//        session.setAttribute("reg","block");
        String user = (String) session.getAttribute("reg_user");
        String password = (String) session.getAttribute("reg_password");
        String email = (String) session.getAttribute("reg_email");
//        String email_code = req.getParameter("reg_email_code");
        if(email_code.equals("")){
            session.setAttribute("code_tips","验证码不能为空");
        }
        else if(!email_code.equals(session.getAttribute("email_code"))){
            session.setAttribute("code_tips","验证码错误，请仔细检查邮箱与验证码是否正确");
        }
        else if(email_code.equals(session.getAttribute("email_code"))){

            char[] id = session.getId().toCharArray();
            StringBuilder id_last6 = new StringBuilder();
            for (int i=0;i < 6;i++) {
                id_last6.insert(0,id[id.length-1-i]);
            }
            String name = "新用户"+ id_last6;
            LocalDate date = LocalDate.now();
            userService.reg(user,password,name,String.valueOf(date),email);
            session.setAttribute("reg_notice","block");
//            super.processTemplate("reg_emailCode",req,resp);
            session.setAttribute("email_code",null);
            session.setAttribute("reg_email",null);
            session.setAttribute("reg_user",null);
            session.setAttribute("reg_password",null);
            session.setAttribute("reg_tips",null);
            session.setAttribute("reg",null);
            session.setAttribute("tips","成功注册账号");
        }
        return "reg_emailCode";
    }
}
