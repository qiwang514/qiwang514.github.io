package com.wq.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wq.seckill.mapper.OrderMapper;

import com.wq.seckill.pojo.Order;

import com.wq.seckill.pojo.SeckillGoods;
import com.wq.seckill.pojo.SeckillOrder;
import com.wq.seckill.pojo.User;
import com.wq.seckill.service.OrderService;

import com.wq.seckill.service.SeckillGoodsService;
import com.wq.seckill.service.SeckillOrderService;
import com.wq.seckill.util.MD5Util;
import com.wq.seckill.util.UUIDUtil;
import com.wq.seckill.vo.GoodsVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl
        extends ServiceImpl<OrderMapper, Order>
        implements OrderService {


    @Resource
    private SeckillGoodsService seckillGoodsService;



    @Resource
    private OrderMapper orderMapper;


    @Resource
    private SeckillOrderService seckillOrderService;


    @Resource
    private RedisTemplate redisTemplate;

    //库存量还有 在此之前已经完成判断
    @Override
    @Transactional
    public Order seckill(User user, GoodsVo goodsVo) {
       //完成秒杀 完成库存查询  需要对DB操作 因此要装配对象
        SeckillGoods seckillGoods =
                seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId()));




        //完成秒杀 不具备原子性 后续还会优化
//        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
//        seckillGoodsService.updateById(seckillGoods);
//        =========================================================================
        //在默认的事务隔离级别 可重复读
        //其隔离级别 会在执行update语句时  在事务中锁定要更新的行 从而防止其他会话在同一行执行update del
        boolean b = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count=stock_count-1")
                .eq("goods_id", goodsVo.getId())
                .gt("stock_count", 0));
        //只有在更新成功时 b才为true
        if (!b){
            //把秒杀失败的信息 记录到redis

            return null;
        }
        //虽然更新失败 但是无论如何都会去走sql数据库


        //生成普通订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());



        //保存order信息
        orderMapper.insert(order);


        //生成秒杀商品订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsVo.getId());
        //秒杀商品订单对应的OrderId  是从上面 添加 order订单获取的
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setUserId(user.getId());



        //保存seckillOrder
        seckillOrderService.save(seckillOrder);
        //将生成的秒杀订单 存到redis 在查询用户是否复购 直接到redis查询即可 优化效果
        //装配对应的redis
        //秒杀订单的key=order:用户id:商品id
        redisTemplate.opsForValue()
                .set("order:"+user.getId()+":"+goodsVo.getId(),seckillOrder);



        return order;


    }



    //方法 生成秒杀路径的值（唯一）

    @Override
    public String createPath(User user, Long goodsId) {
//        生成一个秒杀路径
        String path = MD5Util.getMD5(UUIDUtil.uuid());
        //保存到redis中  设置超时时间60s
        //key设计：seckillPath:userId:goodsId
        redisTemplate.opsForValue().
                set("seckillPath:"+user.getId()+":"+goodsId,path,60, TimeUnit.SECONDS);

        return path;
    }


    //方法 对秒杀路径进行校验
    @Override
    public Boolean checkPath(User user, Long goodsId, String path) {

        if (user == null || goodsId < 0 || !StringUtils.hasText(path) ){
            return false;
        }

        //取出该用户秒杀该商品的路径 从redis取
        String redisPath = (String) redisTemplate.opsForValue()
                    .get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);


    }

    @Override
    public Boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (user == null || goodsId < 0 || !StringUtils.hasText(captcha) ){
            return false;
        }
        //从redis中取出验证码
        String redisCaptcha =(String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);

        return captcha.equals(redisCaptcha);
    }


}
