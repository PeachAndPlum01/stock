package com.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.entity.ProvinceDistance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 省份地理距离Mapper接口
 */
@Mapper
public interface ProvinceDistanceMapper extends BaseMapper<ProvinceDistance> {

    /**
     * 查询两个省份之间的距离
     * @param provinceA 省份A简称
     * @param provinceB 省份B简称
     * @return 距离记录
     */
    @Select("SELECT * FROM province_distance " +
            "WHERE (province_a = #{provinceA} AND province_b = #{provinceB}) " +
            "OR (province_a = #{provinceB} AND province_b = #{provinceA}) " +
            "LIMIT 1")
    ProvinceDistance selectByProvincePair(@Param("provinceA") String provinceA, @Param("provinceB") String provinceB);

    /**
     * 查询某省份的所有相邻省份
     * @param province 省份简称
     * @return 相邻省份距离列表
     */
    @Select("SELECT * FROM province_distance " +
            "WHERE (province_a = #{province} OR province_b = #{province}) " +
            "AND is_neighbor = 1")
    List<ProvinceDistance> selectNeighborsByProvince(@Param("province") String province);

    /**
     * 查询某省份到其他所有省份的距离
     * @param province 省份简称
     * @return 距离列表
     */
    @Select("SELECT * FROM province_distance " +
            "WHERE (province_a = #{province} OR province_b = #{province}) " +
            "ORDER BY distance_km")
    List<ProvinceDistance> selectAllByProvince(@Param("province") String province);
}
