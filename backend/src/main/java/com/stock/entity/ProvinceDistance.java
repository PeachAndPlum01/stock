package com.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 省份地理距离实体类
 */
@TableName("province_distance")
public class ProvinceDistance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 省份A（简称）
     */
    private String provinceA;

    /**
     * 省份A编码
     */
    private String provinceACode;

    /**
     * 省份B（简称）
     */
    private String provinceB;

    /**
     * 省份B编码
     */
    private String provinceBCode;

    /**
     * 距离（千米）
     */
    private Integer distanceKm;

    /**
     * 是否相邻：0-否，1-是
     */
    private Integer isNeighbor;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvinceA() {
        return provinceA;
    }

    public void setProvinceA(String provinceA) {
        this.provinceA = provinceA;
    }

    public String getProvinceACode() {
        return provinceACode;
    }

    public void setProvinceACode(String provinceACode) {
        this.provinceACode = provinceACode;
    }

    public String getProvinceB() {
        return provinceB;
    }

    public void setProvinceB(String provinceB) {
        this.provinceB = provinceB;
    }

    public String getProvinceBCode() {
        return provinceBCode;
    }

    public void setProvinceBCode(String provinceBCode) {
        this.provinceBCode = provinceBCode;
    }

    public Integer getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Integer distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getIsNeighbor() {
        return isNeighbor;
    }

    public void setIsNeighbor(Integer isNeighbor) {
        this.isNeighbor = isNeighbor;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
