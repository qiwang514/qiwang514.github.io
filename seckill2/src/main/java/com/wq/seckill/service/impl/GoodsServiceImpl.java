package com.wq.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wq.seckill.mapper.GoodsMapper;
import com.wq.seckill.mapper.UserMapper;
import com.wq.seckill.pojo.Goods;
import com.wq.seckill.pojo.User;
import com.wq.seckill.service.GoodsService;
import com.wq.seckill.service.UserService;
import com.wq.seckill.vo.GoodsVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {


    @Resource
    //装配GoodsMapper
    private GoodsMapper goodsMapper;



    @Override
    public List<GoodsVo> findGoodsVo() {

        return goodsMapper.findGoodsVo();
    }


    //根据id返回详情
    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }
}
