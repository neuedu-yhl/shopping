package com.neuedu.controller.back;

import com.neuedu.common.Const;
import com.neuedu.common.HigherResponse;
import com.neuedu.pojo.Product;
import com.neuedu.pojo.User;
import com.neuedu.service.AdminService;
import com.neuedu.vo.PrpductDtailVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class AdminCon {

    @Autowired
    private AdminService as;


    /**
     * 管理员登录
     */
    @ResponseBody
    @RequestMapping("/login.do")
    public HigherResponse adminLogin(String userName, String psw, HttpSession session)
    {
        return as.login(userName,psw,session);
    }

    /**
     * 后台  添加商品或更新商品
     */
    @ResponseBody
    @RequestMapping("/saveOrUpdate.do")
    public HigherResponse saveOrUpdateDo(HttpSession session, Product product)
    {
        //Step.1  查看是否有用户登录
        User user = (User)session.getAttribute(Const.CURRENTADMIN);
        if(user == null)
        {
            return HigherResponse.getResponseFailed("未登录,请登录使用。。。");
        }
        if(!StringUtils.isNotBlank(product.getName()))
        {
            return HigherResponse.getResponseFailed("您还没有输入信息...");
        }
        //Step.2  执行添加或者修改操作
        return as.addOrUpdatePro(product);
    }


    /**
     * 产品上下架
     */
    @ResponseBody
    @RequestMapping("/set_sale_status.do")
    public HigherResponse setSaleStatus(HttpSession session,Integer proId,Integer status)
    {
        //Step.1  查看是否有用户登录
        User user = (User)session.getAttribute(Const.CURRENTADMIN);
        if(user == null)
        {
            return HigherResponse.getResponseFailed("未登录,请登录使用。。。");
        }
        return as.updateProStatus(proId,status);
    }

    /**
     * 查看产品详情
     */
    @ResponseBody
    @RequestMapping("/detail.do")
    public HigherResponse<PrpductDtailVO> detail(HttpSession session,Integer proId)
    {
        //Step.1  查看是否有用户登录
        User user = (User)session.getAttribute(Const.CURRENTADMIN);
        if(user == null)
        {
            return HigherResponse.getResponseFailed("未登录,请登录使用。。。");
        }
        return as.detailDo(proId);
    }

    /**
     * 分页查询产品接口
     */
    @ResponseBody
    @RequestMapping("/list.do")
    public HigherResponse proList(HttpSession session, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "2") Integer pageSize)
    {
        //Step.1  查看是否有用户登录
        User user = (User)session.getAttribute(Const.CURRENTADMIN);
        if(user == null)
        {
            return HigherResponse.getResponseFailed("未登录,请登录使用。。。");
        }
        return as.listDo(pageNum,pageSize);
    }

    /**
     * 根据商品名和商品ID查询商品
     */
    @RequestMapping("/search.do")
    @ResponseBody
    public HigherResponse queryListByProNameAndProId(HttpSession session,String proName,Integer proId,@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "2") Integer pageSize)
    {
        //Step.1  查看是否有用户登录
        User user = (User)session.getAttribute(Const.CURRENTADMIN);
        if(user == null)
        {
            return HigherResponse.getResponseFailed("未登录,请登录使用。。。");
        }
        return as.queryProByNameOrId(proName,proId,pageNum,pageSize);
    }
}