package com.neuedu.service;

import com.neuedu.common.HigherResponse;
import com.neuedu.pojo.User;

import javax.servlet.http.HttpSession;

public interface UserService {

    //根据用户名和密码查询用户信息
    public HigherResponse login(String username, String psw, HttpSession session);


    //注册
    public HigherResponse register(User user);


    //获取密保问题
    public HigherResponse forgetPsw_Ques(String userName);


    //提交问题答案
    public HigherResponse forgetCheckAnswer(String username,String question,String answer);


    //忘记密码的重置密码
    public HigherResponse forgetResetPassword(String username,String newpsw,String token);


    //登录状态下的重置密码
    public HigherResponse ResetPassword(HttpSession session,String oldpsw,String newpsw);

    //登录状态下修改个人信息
    public HigherResponse UpdateInfomation(HttpSession session,User user);
}
