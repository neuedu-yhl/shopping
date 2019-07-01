package com.neuedu.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.neuedu.common.Const;
import com.neuedu.common.HigherResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.dao.UserMapper;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.Product;
import com.neuedu.pojo.User;
import com.neuedu.util.TimeUtil;
import com.neuedu.vo.ProductListVO;
import com.neuedu.vo.PrpductDtailVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private UserMapper um;

    @Autowired
    private ProductMapper pm;

    @Autowired
    private CategoryMapper cm;

    @Override
    public HigherResponse login(String username, String psw, HttpSession session) {
        if(username == null || username.length() == 0)
        {
            return HigherResponse.getResponseFailed("用户名不能为空!!!");
        }
        if(psw  == null || psw.length() == 0)
        {
            return HigherResponse.getResponseFailed("密码不能为空!!!");
        }
        int i = um.selectUserByUserName(username);
        if(i == 0)
        {
            return HigherResponse.getResponseFailed("用户名不存在,请重新输入用户名.");
        }
        User user = um.selectUserInfoByUserNameAndPsw(username, psw);
        if(user != null)
        {
            if(user.getRole().intValue() == Const.COMMONADMIN)
            {
                session.setAttribute(Const.CURRENTADMIN,user);
                return HigherResponse.getResponseSuccess("登录成功",user);
            }
        }
        return HigherResponse.getResponseFailed("用户名或密码有误");
    }

    @Override
    public HigherResponse addOrUpdatePro(Product product) {
        //判断商品是否为空
        if(product != null) {
            //设置主图
            // 1.jpg,2.jpg,3.jpg
            // 1.jpg       2.jpg   3.jpg
            String[] split = product.getSubImages().split(",");
            if (split.length > 0) {
                String mainUrl = split[0];
                product.setMainImage(mainUrl);
            }
            //需要区分到底是添加还是修改的操作
            if (product.getId() != null) {
                int i = pm.updateByPrimaryKeySelective(product);
                if (i > 0) {
                    return HigherResponse.getResponseSuccess("修改成功");
                } else {
                    return HigherResponse.getResponseFailed("修改失败...");
                }
            } else {
                int i = pm.insertSelective(product);
                if (i > 0) {
                    return HigherResponse.getResponseSuccess("添加成功");
                } else {
                    return HigherResponse.getResponseFailed("添加失败...");
                }
            }
        }
        return HigherResponse.getResponseFailed("参数有误..");
    }

    @Override
    public HigherResponse updateProStatus(Integer proId, Integer status) {
        if(proId == null)
        {
            return HigherResponse.getResponseFailed("您还没有选中商品");
        }
        if(status == null)
        {
            return HigherResponse.getResponseFailed("您还没有选择商品状态...");
        }
        Product product = new Product();
        product.setStatus(status);
        product.setId(proId);
        int i = pm.updateByPrimaryKeySelective(product);
        if(i>0)
        {
            return HigherResponse.getResponseSuccess("修改成功");
        }
        return HigherResponse.getResponseFailed("修改失败");
    }

    @Override
    public HigherResponse<PrpductDtailVO> detailDo(Integer proId) {
        if(null == proId)
        {
            return HigherResponse.getResponseFailed("商品ID为空...");
        }
        Product product = pm.selectProByProId(proId);
        PrpductDtailVO detailVO = getDetailVO(product);
        return HigherResponse.getResponseSuccess("查看成功",detailVO);
    }

    @Override
    public HigherResponse listDo(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Product> allPros = pm.getAllPros();
        ArrayList<ProductListVO> pvo = Lists.newArrayList();
        for (Product p:allPros) {
            ProductListVO listVO = getListVO(p);
            pvo.add(listVO);
        }
        PageInfo<ProductListVO> objectPageInfo = new PageInfo<>();
        objectPageInfo.setList(pvo);
        return HigherResponse.getResponseSuccess("查询成功",objectPageInfo);
    }

    @Override
    public HigherResponse<ProductListVO> queryProByNameOrId(String proName, Integer pId, Integer pageNum, Integer pageSize) {
        if(StringUtils.isNotBlank(proName))
        {
            proName = new StringBuffer().append("%").append(proName).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        //Step.1 查询数据
        List<Product> prosByNameOrId = pm.getProsByNameOrId(proName, pId);
        ArrayList<ProductListVO> pvo = Lists.newArrayList();
        for (Product p:prosByNameOrId) {
            ProductListVO listVO = getListVO(p);
            pvo.add(listVO);
        }
        PageInfo<ProductListVO> objectPageInfo = new PageInfo<>();
        objectPageInfo.setList(pvo);
        return HigherResponse.getResponseSuccess("查询成功",objectPageInfo);
    }

    //创建构建productListVO的方法
    public ProductListVO getListVO(Product product)
    {
        ProductListVO productListVO = new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setName(product.getName());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setSubTitle(product.getSubtitle());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus().byteValue());
        return productListVO;
    }


    //创建构建vo的方法
    public PrpductDtailVO getDetailVO(Product product)
    {
        PrpductDtailVO productDtailVO = new PrpductDtailVO();
        productDtailVO.setId(product.getId());
        productDtailVO.setCategoryId(product.getCategoryId());
        //需要根据类别id查询父类id的方法
        Category category = cm.selectByPrimaryKey(product.getCategoryId());
        if(null == category.getParentId())
        {
            productDtailVO.setParentCategoryId(0);
        }else
        {
            productDtailVO.setParentCategoryId(category.getParentId());
        }
        productDtailVO.setName(product.getName());
        productDtailVO.setSubTitle(product.getSubtitle());
        productDtailVO.setImageHost(Const.IMAGEHOST);
        productDtailVO.setMainImage(product.getMainImage());
        productDtailVO.setSubImages(product.getSubImages());
        productDtailVO.setDetail(product.getDetail());
        productDtailVO.setPrice(product.getPrice());
        productDtailVO.setStock(product.getStock());
        productDtailVO.setStatus(product.getStatus().byteValue());
        productDtailVO.setCreateTime(TimeUtil.dateToStr(product.getCreateTime(),"yyyy-MM-dd hh:mm:ss"));
        productDtailVO.setUpdateTime(TimeUtil.dateToStr(product.getUpdateTime(),"yyyy-MM-dd hh:mm:ss"));
        return productDtailVO;
    }
}
