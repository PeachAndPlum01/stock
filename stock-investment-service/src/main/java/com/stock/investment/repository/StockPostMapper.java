package com.stock.investment.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.investment.entity.StockPost;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockPostMapper extends BaseMapper<StockPost> {
}
