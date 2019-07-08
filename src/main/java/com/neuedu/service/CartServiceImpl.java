package com.neuedu.service;

import com.google.common.collect.Lists;
import com.neuedu.common.Const;
import com.neuedu.common.HigherResponse;
import com.neuedu.dao.CartMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.pojo.Cart;
import com.neuedu.pojo.Product;
import com.neuedu.util.BigDecimalUtils;
import com.neuedu.vo.CartProductVo;
import com.neuedu.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartSerivce {

    @Autowired
    private CartMapper cm;

    @Autowired
    private ProductMapper pm;

    @Override
    public HigherResponse<CartVo> getCartList(Integer userId) {
        if(null == userId)
        {
            return HigherResponse.getResponseFailed("用户ID不能为空");
        }
        CartVo cartVo = getCartVo(userId);
        return HigherResponse.getResponseSuccess("获取成功",cartVo);
    }

    @Override
    public HigherResponse<CartVo> addCart(Integer userId, Integer proId, Integer count) {
        if(null == proId)
        {
            return HigherResponse.getResponseFailed("商品id不能为空");
        }
        //判断购物车有无该商品
        Cart c = cm.queryProExistsByCart(userId, proId);
        if(null == c)
        {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(proId);
            cart.setQuantity(count);
            cart.setChecked(Const.CARTCHECKED);
            int i1 = cm.insertSelective(cart);
            if(i1 == 0)
            {
                return HigherResponse.getResponseFailed("添加失败。。");
            }
        }else
        {
            //购物车存在相应的商品
            Integer quantity = c.getQuantity();
            quantity = quantity + count;
            c.setQuantity(quantity);
            int i = cm.updateByPrimaryKeySelective(c);
            if(0 == i)
            {
                return HigherResponse.getResponseFailed("修改失败。。。");
            }
        }
        return HigherResponse.getResponseSuccess("添加成功",getCartList(userId));
    }

    @Override
    public HigherResponse<CartVo> selectPro(Integer userId, Integer proId) {
        if(null == proId)
        {
            return HigherResponse.getResponseFailed("您还没有选中商品...");
        }
        //修改购物车中商品的状态
        int i = cm.updateByUserIdAndProId(userId, proId, Const.CARTCHECKED);
        if(i > 0)
        {
            return HigherResponse.getResponseSuccess("选择成功",this.getCartList(userId));
        }
        return HigherResponse.getResponseFailed("选择失败...");
    }


    //获取CartVO的方法
    private CartVo getCartVo(Integer userId)
    {
        //构建一个购物车对象
        CartVo cartVo = new CartVo();
        //创建集合存放购物车商品
        ArrayList<CartProductVo> cpv = Lists.newArrayList();
        //根据userID查询购物车信息
        List<Cart> carts = cm.selectByUserId(userId);
        //创建购物车总价格
        BigDecimal allProPrice = new BigDecimal("0");
        if(null != carts)
        {
            for(Cart c:carts)
            {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(c.getId());
                cartProductVo.setUserId(c.getUserId());
                cartProductVo.setProId(c.getProductId());
                //根据商品id查询商品信息
                Product product = pm.selectProByProId(c.getProductId());
                if(null != product)
                {
                    cartProductVo.setProName(product.getName());
                    cartProductVo.setProSubtitle(product.getSubtitle());
                    cartProductVo.setMainImage(product.getMainImage());
                    cartProductVo.setPrice(product.getPrice().doubleValue());
                    cartProductVo.setStatus(product.getStatus());
                    cartProductVo.setStock(product.getStock());
                    cartProductVo.setIsChecked(c.getChecked());
                    Integer count = 0;
                    //判断如果购买数量小于库存 说明 库存充足
                    if(c.getQuantity() <= product.getStock())
                    {
                           count = c.getQuantity();
                           cartProductVo.setLimitQuality("有货");
                    }else {
                        count = product.getStock();
                        cartProductVo.setLimitQuality("库存不足,您兜底了...");
                        //修改cart中的库存
                        Cart cart = new Cart();
                        cart.setId(c.getId());
                        cart.setQuantity(count);
                        cm.updateByPrimaryKeySelective(cart);
                    }
                    //将最后计算的数量设置到cartProductVo里面
                    cartProductVo.setQuality(count);
                    //购物车一件商品的总价
                    cartProductVo.setTotalPrice(BigDecimalUtils.multipy(cartProductVo.getPrice().doubleValue(),cartProductVo.getQuality().doubleValue()));
                    //添加到集合
                    cpv.add(cartProductVo);
                }
                allProPrice = BigDecimalUtils.plus(allProPrice.doubleValue(),cartProductVo.getTotalPrice().doubleValue());
            }
        }
        cartVo.setCpvs(cpv);
        cartVo.setTotalPrice(allProPrice);
        cartVo.setImgHost(Const.PREFIX);
        cartVo.setAllChecked(queryIsChecked(userId));
        return cartVo;
    }
    //判断是否全选
    private boolean queryIsChecked(Integer userId)
    {
        int i = cm.lookIsAllChecked(userId);
        return  i==0;
    }
}
