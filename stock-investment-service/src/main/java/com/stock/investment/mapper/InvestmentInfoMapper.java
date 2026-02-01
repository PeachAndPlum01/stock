package com.stock.investment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.investment.entity.InvestmentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 投资信息Mapper接口
 */
@Mapper
public interface InvestmentInfoMapper extends BaseMapper<InvestmentInfo> {

    /**
     * 按省份统计股票数量
     */
    @Select("SELECT province, COUNT(*) as count FROM investment_info WHERE status = 1 GROUP BY province")
    List<Map<String, Object>> countByProvince();

    /**
     * 获取涨幅前N的股票
     */
    @Select("SELECT * FROM investment_info WHERE status = 1 ORDER BY ten_day_change DESC LIMIT #{limit}")
    List<InvestmentInfo> getTopGainers(int limit);

    /**
     * 按省份获取涨幅前N的股票
     */
    @Select("SELECT * FROM investment_info WHERE status = 1 AND province = #{province} ORDER BY ten_day_change DESC LIMIT #{limit}")
    List<InvestmentInfo> getTopGainersByProvince(String province, int limit);
}
