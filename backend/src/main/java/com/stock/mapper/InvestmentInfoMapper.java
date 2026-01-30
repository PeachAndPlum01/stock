package com.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.entity.InvestmentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 投资信息Mapper接口
 */
@Mapper
public interface InvestmentInfoMapper extends BaseMapper<InvestmentInfo> {

    /**
     * 根据省份查询最近的投资信息
     */
    @Select("SELECT * FROM investment_info WHERE province = #{province} AND status = 1 " +
            "ORDER BY investment_date DESC, create_time DESC LIMIT #{limit}")
    List<InvestmentInfo> selectRecentByProvince(String province, Integer limit);

    /**
     * 查询所有有效的投资信息
     */
    @Select("SELECT * FROM investment_info WHERE status = 1 ORDER BY investment_date DESC")
    List<InvestmentInfo> selectAllActive();
}
