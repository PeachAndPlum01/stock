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

    /**
     * 根据关联省份查询投资信息
     * @param province 省份简称
     * @return 包含该省份在关联字段中的投资信息
     */
    @Select("SELECT * FROM investment_info WHERE status = 1 " +
            "AND (related_provinces LIKE CONCAT('%', #{province}, '%') OR province = #{province})")
    List<InvestmentInfo> selectByRelatedProvince(String province);

    /**
     * 查询两个省份都关联的投资信息
     * @param provinceA 省份A简称
     * @param provinceB 省份B简称
     * @return 两个省份都关联的投资信息列表
     */
    @Select("SELECT * FROM investment_info WHERE status = 1 " +
            "AND ((province = #{provinceA} AND related_provinces LIKE CONCAT('%', #{provinceB}, '%')) " +
            "OR (province = #{provinceB} AND related_provinces LIKE CONCAT('%', #{provinceA}, '%')) " +
            "OR (related_provinces LIKE CONCAT('%', #{provinceA}, '%') " +
            "AND related_provinces LIKE CONCAT('%', #{provinceB}, '%')))")
    List<InvestmentInfo> selectJointByProvincePair(String provinceA, String provinceB);

    /**
     * 根据省份查询近十日涨幅最高的8个股票
     * @param province 省份简称
     * @return 股票列表（按涨幅降序排序，限制8条）
     */
    @Select("SELECT * FROM investment_info WHERE province = #{province} AND status = 1 " +
            "AND ten_day_change IS NOT NULL " +
            "ORDER BY ten_day_change DESC LIMIT 8")
    List<InvestmentInfo> selectTop8ByTenDayChange(String province);

    /**
     * 根据省份查询近十日涨幅最高的5个股票
     * 用于计算省份相关度
     * @param province 省份简称
     * @return 股票列表（按涨幅降序排序，限制5条）
     */
    @Select("SELECT * FROM investment_info WHERE province = #{province} AND status = 1 " +
            "AND ten_day_change IS NOT NULL " +
            "ORDER BY ten_day_change DESC LIMIT 5")
    List<InvestmentInfo> selectTop5ByTenDayChange(String province);

    /**
     * 根据省份查询近十日涨幅最高的3个股票
     * 用于生成关联原因说明
     * @param province 省份简称
     * @return 股票列表（按涨幅降序排序，限制3条）
     */
    @Select("SELECT * FROM investment_info WHERE province = #{province} AND status = 1 " +
            "AND ten_day_change IS NOT NULL " +
            "ORDER BY ten_day_change DESC LIMIT 3")
    List<InvestmentInfo> selectTop3ByTenDayChange(String province);
}
