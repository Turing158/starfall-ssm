package com.starfall.controller;

import com.starfall.config.sf_config;
import com.starfall.enity.User;
import com.starfall.service.DiscussService;
import com.starfall.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Objects;

@MultipartConfig
@Controller
public class SetController {
    @RequestMapping("/set")
    public String Set(
            HttpSession session
    ){
        ApplicationContext context = new AnnotationConfigApplicationContext(sf_config.class);
        UserService userService = context.getBean("userService", UserService.class);
//        HttpSession session = req.getSession();
        if (session.getAttribute("display_me") == null){
            session.setAttribute("display_me","block");
            session.setAttribute("display_i","none");
            session.setAttribute("display_p","none");
            session.setAttribute("display_h","none");
        }
        String user_session = (String) session.getAttribute("user");
        User user = userService.getInfo(user_session);
        session.setAttribute("name",user.getName());
        session.setAttribute("date",user.getData());
        session.setAttribute("introduce",user.getIntroduce());
        session.setAttribute("email",user.getEmail());
        session.setAttribute("level",user.getLevel());
        return "set";
    }





    @RequestMapping("/upload_head")
    public String setHead(
            HttpSession session,
            HttpServletRequest req,
            HttpServletResponse resp

    ) throws IOException, ServletException {
        ApplicationContext context = new AnnotationConfigApplicationContext(sf_config.class);
        UserService userService = context.getBean("userService", UserService.class);
        DiscussService discussService = context.getBean("discussService", DiscussService.class);
//        HttpSession session = req.getSession();
        session.setAttribute("display_me","none");
        session.setAttribute("display_i","none");
        session.setAttribute("display_p","none");
        session.setAttribute("display_h","block");
//        改编码，以免乱码
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
//        直接获取input的name的值
        Part part = req.getPart("head_img");
//        这个是保存头像的，现在保存只能临时的，这个填存服务器头像的存储位置，可永久保存
//        String savePath = "";
        String user = (String) session.getAttribute("user");
//        获取文件后缀
        String fileType = part.getSubmittedFileName().substring(part.getSubmittedFileName().lastIndexOf("."));
//        设置存储位置
        String path = req.getServletContext().getRealPath("head_img/");
        System.out.println(path);
//        System.out.println(req.getRealPath(req.getServletPath()));
//        写入保存的路径
        part.write(path+user+fileType);
//        永久保存头像，避免服务器崩溃导致丢失
//        part.write(savePath+user+fileType);
        userService.setHead(user,user+fileType);
//        更新数据库里头像的名字
        discussService.updateHead();
        session.setAttribute("head",user+fileType);
        resp.sendRedirect("/set");
        return "redirect:/set";
    }


    @RequestMapping("/set_information")
    public String setInformation(
            HttpSession session,
            HttpServletRequest req,
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "introduce",required = false) String introduce,
            @RequestParam(value = "seti_code",required = false) String code
    ) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(sf_config.class);
        UserService userService = context.getBean("userService", UserService.class);
//        HttpSession session = req.getSession();
        session.setAttribute("display_me","none");
        session.setAttribute("display_p","none");
        session.setAttribute("display_i","block");
        req.setCharacterEncoding("utf-8");
        String user = (String) session.getAttribute("user");
//        String name = req.getParameter("name");
//        String introduce = req.getParameter("introduce");
//        String code = req.getParameter("seti_code");
        if(Objects.equals(code,"")){
            session.setAttribute("i_tips","验证码不能为空");
        }
        else if(!Objects.equals(code,session.getAttribute("code"))){
            session.setAttribute("i_tips","验证码错误");
        }
        else if ((name != null || introduce != null )&& Objects.equals(code,session.getAttribute("code"))){
            session.setAttribute("i_tips","信息修改成功");
            userService.updateInformation(user,name,introduce);
        }
        session.setAttribute("code",null);
        return "redirect:/set";
    }



    @RequestMapping("/set_password")
    public String setPassword(
            HttpSession session,
            HttpServletRequest req,
            @RequestParam(value = "old_password",required = false) String old_password,
            @RequestParam(value = "new_password",required = false) String new_password,
            @RequestParam(value = "seti_VerifyCode",required = false) String code

    ) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(sf_config.class);
        UserService userService = context.getBean("userService", UserService.class);
//        HttpSession session = req.getSession();
        req.setCharacterEncoding("utf-8");
        String user = (String) session.getAttribute("user");
//        String old_password = req.getParameter("old_password");
//        String new_password = req.getParameter("new_password");
//        String code = req.getParameter("seti_VerifyCode");
        session.setAttribute("display_me","none");
        session.setAttribute("display_p","block");
        session.setAttribute("display_i","none");
        boolean flag = userService.checkOldPassword(user,old_password);
        if(flag && Objects.equals(code,session.getAttribute("code"))){
            session.setAttribute("p_tips","密码修改成功");
            session.setAttribute("p_tips_color","lightgreen");
            userService.setNewPassword(user,new_password);
            session.setAttribute("code",null);
        }
        else if(!Objects.equals(code,session.getAttribute("code"))){
            session.setAttribute("p_tips","验证码错误");
            session.setAttribute("p_tips_color","rgb(255, 125, 125)");
        }
        else{
            session.setAttribute("p_tips","原密码错误");
            session.setAttribute("p_tips_color","rgb(255, 125, 125)");
        }
        return "redirect:/set";
    }
}
