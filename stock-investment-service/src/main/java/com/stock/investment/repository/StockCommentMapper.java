package com.stock.investment.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.investment.entity.StockComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockCommentMapper extends BaseMapper<StockComment> {
}
