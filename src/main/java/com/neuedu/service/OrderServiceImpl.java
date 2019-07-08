package com.neuedu.service;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.alipay.trade.Main;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.neuedu.common.Const;
import com.neuedu.common.HigherResponse;
import com.neuedu.dao.*;
import com.neuedu.pojo.*;
import com.neuedu.util.BigDecimalUtils;
import com.neuedu.util.FTPUtils;
import com.neuedu.vo.OrderItemVo;
import com.neuedu.vo.OrderVo;
import com.neuedu.vo.ShippingVo;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {


    //创建支付的成员变量开始============================
    private static Log log = LogFactory.getLog(Main.class);

    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;

    // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
    private static AlipayTradeService tradeWithHBService;

    // 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
    private static AlipayMonitorService monitorService;



    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
                .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
                .setFormat("json").build();
    }







    //结束===================================================








    //将CartMapper注入
    @Autowired
    private CartMapper cm;

    //将ProductMapper注入
    @Autowired
    private ProductMapper pm;


    //将OrderMapper注入
    @Autowired
    private OrderMapper om;

    //将OrderItemMapper注入
    @Autowired
    private OrderItemMapper oim;


    //将shippingMapper注入
    @Autowired
    private ShippingMapper shippingMapper;



    //取消订单
    @Override
    public HigherResponse cancelOrder(Long orderNo) {
        if(null == orderNo)
        {
            return HigherResponse.getResponseFailed("您还没有下单...");
        }
        //查询订单信息
        Order orderByOrderNo = om.getOrderByOrderNo(orderNo);
        //拿到订单状态
        Integer status = orderByOrderNo.getStatus();
        if(status != Const.NOPAYSTATUS)
        {
            return HigherResponse.getResponseFailed("订单不能取消..");
        }
        //取消订单
        Order order = new Order();
        order.setId(orderByOrderNo.getId());
        order.setStatus(Const.CANCELORDER);
        int i = om.updateByPrimaryKeySelective(order);
        if(i > 0)
        {
            return HigherResponse.getResponseSuccess("取消订单成功,订单编号为:"+orderNo);
        }
        return HigherResponse.getResponseFailed("取消订单失败。。。");
    }

    @Override
    public HigherResponse getOrderDetail(Long orderNo) {
        if(null == orderNo)
        {
            return HigherResponse.getResponseFailed("您还没有下单...");
        }
        //查询订单信息
        Order orderByOrderNo = om.getOrderByOrderNo(orderNo);

        //根据orderNo查询orderItemList
        List<OrderItem> orderItem = oim.getOrderItem(orderNo);

        OrderVo orderVo = getOrderVo(orderByOrderNo, orderItem, orderByOrderNo.getShippingId());
        if(null == orderVo)
        {
            return HigherResponse.getResponseFailed("查询明细失败..");
        }
        return HigherResponse.getResponseSuccess("查询成功",orderVo);
    }

    @Override
    public HigherResponse payDo(Integer userId, Long orderNo) {
        //Step .1 根据用户ID和订单编号查询订单
        Order orderByUserIdAndOrderNo = om.getOrderByUserIdAndOrderNo(userId, orderNo);
        if(null == orderByUserIdAndOrderNo)
        {
            return HigherResponse.getResponseFailed("查询不到订单。");
        }
        return (HigherResponse) trade_precreate(orderByUserIdAndOrderNo);
    }


    //==================================================================生成二维码开始



    //打印日志的方法
    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }







    // 测试当面付2.0生成支付二维码
    public Object trade_precreate(Order order) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "睿乐Go商城"+order.getOrderNo()+"当面扫码消费";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "购买商品共"+order.getPayment().toString()+"元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，

        //查询订单编号下的购买的商品详细信息
        List<OrderItem> orderItems = oim.getOrderItem(order.getOrderNo());
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        if(null != orderItems)
        {
            for(OrderItem orderItem:orderItems)
            {
                GoodsDetail goodsDetail = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(), orderItem.getCurrentUnitPrice().longValue(), orderItem.getQuantity());
                goodsDetailList.add(goodsDetail);
            }
        }



        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
//        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
        // 创建好一个商品后添加至商品明细列表
//        goodsDetailList.add(goods1);

        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
//        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
//        goodsDetailList.add(goods2);

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl("http://sdcuzv.natappfree.cc/portal/order/callback.do")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                // 需要修改为运行机器上的路径
                String filePath = String.format("E:\\imgs\\qr-%s.png",
                        response.getOutTradeNo());
                log.info("filePath:" + filePath);
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                //将二维码图片传到FTP上
                File file = new File(filePath);
                boolean b = new FTPUtils().uploadFile("/file/", Lists.<File>newArrayList(file));
                HashMap<String, String> objectHashMap = Maps.newHashMap();
                objectHashMap.put("orderNo",order.getOrderNo().toString());
                objectHashMap.put("qrCodePath", Const.PREFIX+":81/file/"+file.getName());
                file.delete();
                return HigherResponse.getResponseSuccess("生产二维码成功",objectHashMap);
            case FAILED:
                log.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        return HigherResponse.getResponseFailed("生成二维码失败..");
    }



















    //==================================================================生产二维码结束
















    @Override
    public HigherResponse createOrder(Integer userId, Integer shippingId) {
//        Step2: 根据用户ID查询购物车中已选中的商品
//        Step2.5: 将List<Cart> 转为 List<OrderItem>
//        Step3: 将Order写入数据库
//        Step4: 将OrderItem写入数据库

        List<Cart> carts = cm.selectCheckedByUserId(userId);
        System.out.println("carts:------------------------------------------"+carts);
        //判断如果为null的话
        if(null == carts)
        {
            return HigherResponse.getResponseFailed("您还没有选中商品...");
        }
        HigherResponse higherResponse = cartToOrderItem(carts, userId);
        //计算订单总金额
       BigDecimal orderTotalPrice = getOrderTotalPrice(higherResponse);

        //创建Order
        Order newOrder = getNewOrder(userId, shippingId, orderTotalPrice);

        //给OrderItems赋ordeNo值
        Long orderNo = newOrder.getOrderNo();
       List<OrderItem> orderItems = (List<OrderItem>)higherResponse.getData();
       for(OrderItem orderItem:orderItems)
       {
           orderItem.setOrderNo(orderNo);
       }
        //写入数据库
        //批量添加
       int i = oim.insertBatchOrderItems(orderItems);
        if(i == 0)
        {
           return HigherResponse.getResponseFailed("添加订单子项失败...");
       }
        //Step5: 减库存
        minusStock(orderItems);
        //Step6: 清空购物车中已选中的商品
        clearCartCheckedShop(orderItems,userId);
        //Step7: 返回OrderVo
       OrderVo orderVo = getOrderVo(newOrder, orderItems, shippingId);
        return HigherResponse.getResponseSuccess("创建成功",orderVo);
    }



    //返回OrderVo
    private OrderVo getOrderVo(Order o, List<OrderItem> orderItems, Integer shippingId)
    {
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(o.getOrderNo());
        orderVo.setAllPrice(o.getPayment());
        orderVo.setPaymentType(o.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.getPaymentTypeDesc(Const.PAYMENTTYPEISONLINE));
        orderVo.setPostAge(o.getPostage());
        orderVo.setStatus(o.getStatus());
        orderVo.setStatusDesc(Const.getStatusDesc(Const.NOPAYSTATUS));
        orderVo.setPaymentTime(o.getPaymentTime());
        orderVo.setSendTime(o.getSendTime());
        orderVo.setEndTime(o.getEndTime());
        orderVo.setCloseTime(o.getCloseTime());
        orderVo.setCreateTime(o.getCreateTime());
        //构建orderItemVoList
        ArrayList<OrderItemVo> orderItemVos = Lists.newArrayList();
        for(OrderItem orderItem:orderItems)
        {
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setOrderNo(orderItem.getOrderNo());
            orderItemVo.setProductId(orderItem.getProductId());
            orderItemVo.setProductName(orderItem.getProductName());
            orderItemVo.setProductImage(orderItem.getProductImage());
            orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
            orderItemVo.setQuantity(orderItem.getQuantity());
            orderItemVo.setTotalPrice(orderItem.getTotalPrice());
            orderItemVos.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVos);
        orderVo.setImageHost("www.hello.qwe");
        ShippingVo shippingVo1 = getShippingVo(shippingId);
        orderVo.setShippingVo(shippingVo1);
        return orderVo;
    }
    // 构建ShippingVo的方法
    private ShippingVo getShippingVo(Integer shippingId)
    {
        ShippingVo shippingVo = new ShippingVo();
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        shippingVo.setReveiverName(shipping.getReceiverName());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReveiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverProvience(shipping.getReceiverProvince());
        shippingVo.setReveiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReveiverAddress(shipping.getReceiverAddress());
        shippingVo.setReveiverZip(shipping.getReceiverZip());
        return shippingVo;
    }


    //清空购物车中已选中的商品
    private void clearCartCheckedShop(List<OrderItem> orderItems,Integer userId)
    {
        //批量删除
        cm.delCartCheckedShopByUserId(orderItems,userId);
    }

    //减库存
    private void minusStock(List<OrderItem> orderItems)
    {
        for (OrderItem orderItem:orderItems)
        {
            //购买的数量
            Integer quantity = orderItem.getQuantity();
            //商品有多少库存
            Integer productId = orderItem.getProductId();
            Product product = pm.selectProByProId(productId);
            //商品的库存
            Integer stock = product.getStock();
            product.setStock(stock-quantity);
            //修改Stock
            pm.updateByPrimaryKeySelective(product);
        }
    }



    //计算订单总金额
    private BigDecimal getOrderTotalPrice(HigherResponse response)
    {
        List<OrderItem> orderItems = (List<OrderItem>)response.getData();
        System.out.println("orderItems:====================================="+orderItems);
        BigDecimal bigDecimal = new BigDecimal("0");
        for(OrderItem oi:orderItems)
        {
            bigDecimal = BigDecimalUtils.plus(bigDecimal.doubleValue(),oi.getTotalPrice().doubleValue());
        }
        return bigDecimal;
    }


    //创建Order
    private Order getNewOrder(Integer userId, Integer shippingId,BigDecimal totalPrice)
    {
        Order order = new Order();
        //需要一个orderNo
        order.setOrderNo(getOrderNo(userId));
        order.setUserId(userId);
        //订单总金额
        order.setPayment(totalPrice);
        //支付方式
        order.setPaymentType(Const.PAYMENTTYPEISONLINE);
        //订单状态
        order.setStatus(Const.NOPAYSTATUS);
        //收货地址的ID
        order.setShippingId(shippingId);
        //执行添加到Order表中的方法
        int i = om.insertSelective(order);
        if(i > 0)
        {
            return order;
        }
        return null;
    }
    //创建orderNo的方法
    private Long getOrderNo(Integer userId)
    {
        //订单编号生成规则
        long l = System.currentTimeMillis();
        //生成随机数
        int i = new Random().nextInt(100000);
        return l+i+userId;
    }








    //将List<Cart>转为List<OrderItem>
    private HigherResponse cartToOrderItem(List<Cart> carts,Integer userId) {
        ArrayList<OrderItem> orderItems = Lists.newArrayList();
        for (Cart c : carts)
        {
            OrderItem orderItem = new OrderItem();
            orderItem.setUserId(userId);
            orderItem.setProductId(c.getProductId());
            //根据cart里的productId查询对应的商品信息
            Product product = pm.selectProByProId(c.getProductId());
            //判断如果商品在售
            if(product.getStatus() != Const.PRODUCTISSALE)
            {
                return HigherResponse.getResponseFailed("商品已下线...");
            }
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            //判断库存
            if(c.getQuantity() > product.getStock())
            {
                return HigherResponse.getResponseFailed("手慢了...");
            }
            orderItem.setQuantity(c.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtils.multipy(product.getPrice().doubleValue(),c.getQuantity()));
            orderItems.add(orderItem);
        }
        return HigherResponse.getResponseSuccess(orderItems);
    }
}