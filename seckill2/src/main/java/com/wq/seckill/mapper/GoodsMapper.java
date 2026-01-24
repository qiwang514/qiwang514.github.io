package com.wq.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wq.seckill.pojo.Goods;
import com.wq.seckill.vo.GoodsVo;

import java.util.List;

public interface GoodsMapper extends BaseMapper<Goods> {


    //获取秒杀的商品列表
    List<GoodsVo> findGoodsVo();

    //获取商品的详情
    GoodsVo findGoodsVoByGoodsId(Long goodsId);


}
