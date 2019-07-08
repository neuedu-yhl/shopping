package com.neuedu.common;

public class Const {

    public static final String USERNAME = "username";

    public static final String EMAIL = "email";


    public static final Integer COMMONUSER = 0;

    public static final Integer COMMONADMIN = 1;

    public static final String TOKENCOCHE = "token_cache_user";

    public static final String CURRENTUSER = "user";

    public static final String CURRENTADMIN = "admin";

    //图片主机名
    public static final String IMAGEHOST = "www.img.com";

    //FTP服务器的IP
    public static final String SERVERIP ="39.105.183.73";

    //FTP服务器的用户名
    public static final String FTPUSERNAME = "ftpuser";

    //FTP服务器的密码
    public static final String FTPUSERPSW = "yinhaoliang";


    //文件服务器的前缀
    public static final String PREFIX  = "www.hello.qwe";


    //购物车状态为选中
    public static final Integer CARTCHECKED = 1;

    //购物车状态未选中
    public static final Integer CARTUNCHECK = 0;


    //商品销售状态
    public static final Integer PRODUCTISSALE = 1;


    //下单支付类型
    //线上支付
    public static final Integer PAYMENTTYPEISONLINE = 1;

    public static String getPaymentTypeDesc(Integer PAYMENTTYPEISONLINE)
    {
        switch (PAYMENTTYPEISONLINE)
        {
            case 1:
                return "线上支付";
        }
        return null;
    }


    //货到付款
    public static final Integer PAYMENTTYPEGOODS = 2;


    //订单状态   未付款
    public static final Integer NOPAYSTATUS = 10;
    //订单状态   取消订单
    public static final Integer CANCELORDER = 0;

    public static String getStatusDesc(Integer NOPAYSTATUS)
    {
        switch (PAYMENTTYPEISONLINE)
        {
            case 10:
                return "未付款";
        }
        return null;
    }



}
