package com.wq.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.wq.seckill.mapper.SeckillOrderMapper;

import com.wq.seckill.pojo.SeckillOrder;

import com.wq.seckill.service.SeckillOrderService;
import org.springframework.stereotype.Service;

@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder>
        implements SeckillOrderService {
}
