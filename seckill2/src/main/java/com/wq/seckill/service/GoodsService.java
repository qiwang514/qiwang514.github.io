package com.wq.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wq.seckill.pojo.Goods;
import com.wq.seckill.pojo.User;
import com.wq.seckill.vo.GoodsVo;

import java.util.List;


public interface GoodsService extends IService<Goods> {
    //秒杀商品列表
    List<GoodsVo> findGoodsVo();

    //获取商品详情
    GoodsVo findGoodsVoByGoodsId(Long goodsId);

}
