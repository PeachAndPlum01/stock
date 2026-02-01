package com.stock.correlation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.correlation.entity.RelatedProvince;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 省份相关度Mapper接口
 */
@Mapper
public interface RelatedProvinceMapper extends BaseMapper<RelatedProvince> {

    /**
     * 获取指定省份的相关省份列表（按相关度排序）
     */
    @Select("SELECT * FROM related_provinces WHERE source_province = #{province} ORDER BY correlation_score DESC")
    List<RelatedProvince> getRelatedProvinces(String province);

    /**
     * 获取相关度最高的N对省份
     */
    @Select("SELECT * FROM related_provinces ORDER BY correlation_score DESC LIMIT #{limit}")
    List<RelatedProvince> getTopCorrelations(int limit);
}
