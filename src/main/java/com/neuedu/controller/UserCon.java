package com.neuedu.controller;


import com.neuedu.common.HigherResponse;
import com.neuedu.pojo.User;
import com.neuedu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/portal/user")
public class UserCon {

    @Autowired
    private UserService us;
    @ResponseBody
    @RequestMapping("/login.do")
    public HigherResponse login(String userName, String psw, HttpSession session)
    {
        return us.login(userName,psw,session);
    }

    @ResponseBody
    @RequestMapping("/register.do")
    public HigherResponse register(User user)
    {
        return us.register(user);
    }

    @ResponseBody
    @RequestMapping("/forget_get_question.do")
    //查询密保问题
    public HigherResponse queryQues(String userName)
    {
        return us.forgetPsw_Ques(userName);
    }


    /**
     *  提交问题答案
     */
    @ResponseBody
    @RequestMapping("/forget_check_answer.do")
    public HigherResponse forget_check_answer(String username,String question,String answer)
    {
        return us.forgetCheckAnswer(username,question,answer);
    }


    /**
     * 忘记密码中的重设密码
     */
    @ResponseBody
    @RequestMapping("/forget_reset_password.do")
    public HigherResponse forget_reset_password(String username,String newpsw,String token)
    {
         return us.forgetResetPassword(username,newpsw,token);
    }


    /**
     * 登录状态中重置密码
     */
    @ResponseBody
    @RequestMapping("/reset_password.do")
    public HigherResponse reset_password(HttpSession session,String pswOld,String pswNew)
    {
        return us.ResetPassword(session,pswOld,pswNew);
    }

    /**
     * 登录状态下更新个人信息
     */
    @ResponseBody
    @RequestMapping("/update_information.do")
    public HigherResponse update_information(HttpSession session,User user)
    {
        return  us.UpdateInfomation(session,user);
    }
}
