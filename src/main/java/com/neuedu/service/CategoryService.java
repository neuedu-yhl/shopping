package com.neuedu.service;

import com.neuedu.common.HigherResponse;
import com.neuedu.pojo.Category;

public interface CategoryService {

    public HigherResponse getCategory(Integer cId);

    public HigherResponse addOneCategory(Category c);

}
