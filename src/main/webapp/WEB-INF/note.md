返回对象数据格式  
       success:  
            1.  status data  
            2.  status  
            3.  status msg  
            4.  status msg data   
       failed:  `
            1. status msg`
封装高复用的响应对象
    HigherResponse
    
### 1.登录步骤      编写接口
  判断用户名和密码是否为null
  判断用户名是否存在 如果不存在则提示用户名不存在 
  根据用户名和密码查询用户
  如果查询到的用户不为null  登录成功
  否则密码错误  
### 2.注册 
  非空校验  
  判断用户名是否存在   
  判断邮箱是否存在
  密码需要加密(md5)
  注册
  
  
  