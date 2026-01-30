package com.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.entity.RelatedProvince;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 省份相关度Mapper接口
 */
@Mapper
public interface RelatedProvinceMapper extends BaseMapper<RelatedProvince> {

    /**
     * 查询某省份最相关的省份列表
     * @param province 省份简称
     * @param limit 查询数量限制
     * @return 相关省份列表，按相关度降序排列
     */
    @Select("SELECT * FROM related_provinces " +
            "WHERE (source_province = #{province} OR target_province = #{province}) " +
            "AND correlation_score > 0 " +
            "ORDER BY correlation_score DESC LIMIT #{limit}")
    List<RelatedProvince> selectTopRelatedByProvince(@Param("province") String province, @Param("limit") Integer limit);

    /**
     * 查询两个省份之间的相关度
     * @param provinceA 省份A简称
     * @param provinceB 省份B简称
     * @return 相关度记录
     */
    @Select("SELECT * FROM related_provinces " +
            "WHERE (source_province = #{provinceA} AND target_province = #{provinceB}) " +
            "OR (source_province = #{provinceB} AND target_province = #{provinceA}) " +
            "LIMIT 1")
    RelatedProvince selectByProvincePair(@Param("provinceA") String provinceA, @Param("provinceB") String provinceB);

    /**
     * 查询所有省份相关度记录
     * @return 所有省份相关度记录
     */
    @Select("SELECT * FROM related_provinces WHERE correlation_score > 0 ORDER BY correlation_score DESC")
    List<RelatedProvince> selectAllActive();
}
