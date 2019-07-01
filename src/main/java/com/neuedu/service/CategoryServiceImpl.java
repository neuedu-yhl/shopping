package com.neuedu.service;

import com.neuedu.common.HigherResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.pojo.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper cm;

    @Override
    public HigherResponse getCategory(Integer cId) {
        List<Category> category = cm.querySubCategory(cId);
        if(null != category)
        {
            return HigherResponse.getResponseSuccess("查询成功",category);
        }
        return HigherResponse.getResponseFailed("查询失败。。。");
    }

    @Override
    public HigherResponse addOneCategory(Category c) {
        if(!StringUtils.isNotBlank(c.getName()))
        {
            return HigherResponse.getResponseFailed("类别名不能为空...");
        }
        int i = cm.queryCategoryNameIsExists(c.getName());
        if(i>0)
        {
            return HigherResponse.getResponseFailed("类别名已存在...");
        }
        c.setStatus(1);
        int i1 = cm.insertSelective(c);
        if(i1 >0 )
        {
            return HigherResponse.getResponseSuccess("添加类别成功");
        }
        return HigherResponse.getResponseFailed("添加类别失败...");
    }
}
