package com.stock.correlation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stock.correlation.entity.RelatedProvince;
import com.stock.correlation.repository.RelatedProvinceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 省份关联服务
 */
@Service
public class CorrelationService {

    @Autowired
    private RelatedProvinceMapper relatedProvinceMapper;

    /**
     * 获取指定省份的相关省份列表
     */
    public List<RelatedProvince> getRelatedProvinces(String province) {
        return relatedProvinceMapper.getRelatedProvinces(province);
    }

    /**
     * 获取两个省份之间的相关度
     */
    public RelatedProvince getCorrelation(String sourceProvince, String targetProvince) {
        LambdaQueryWrapper<RelatedProvince> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RelatedProvince::getSourceProvince, sourceProvince);
        wrapper.eq(RelatedProvince::getTargetProvince, targetProvince);
        return relatedProvinceMapper.selectOne(wrapper);
    }

    /**
     * 获取相关度最高的N对省份
     */
    public List<RelatedProvince> getTopCorrelations(int limit) {
        return relatedProvinceMapper.getTopCorrelations(limit);
    }

    /**
     * 按相关度范围查询
     */
    public List<RelatedProvince> getByScoreRange(BigDecimal minScore, BigDecimal maxScore) {
        LambdaQueryWrapper<RelatedProvince> wrapper = new LambdaQueryWrapper<>();
        
        if (minScore != null) {
            wrapper.ge(RelatedProvince::getCorrelationScore, minScore);
        }
        if (maxScore != null) {
            wrapper.le(RelatedProvince::getCorrelationScore, maxScore);
        }
        
        wrapper.orderByDesc(RelatedProvince::getCorrelationScore);
        return relatedProvinceMapper.selectList(wrapper);
    }

    /**
     * 获取所有省份关联数据
     */
    public List<RelatedProvince> getAllCorrelations() {
        LambdaQueryWrapper<RelatedProvince> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(RelatedProvince::getCorrelationScore);
        return relatedProvinceMapper.selectList(wrapper);
    }

    /**
     * 按共同概念查询
     */
    public List<RelatedProvince> getByConcept(String concept) {
        LambdaQueryWrapper<RelatedProvince> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(RelatedProvince::getCommonConcepts, concept);
        wrapper.orderByDesc(RelatedProvince::getCorrelationScore);
        return relatedProvinceMapper.selectList(wrapper);
    }

    /**
     * 按共同行业查询
     */
    public List<RelatedProvince> getByIndustry(String industry) {
        LambdaQueryWrapper<RelatedProvince> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(RelatedProvince::getCommonIndustries, industry);
        wrapper.orderByDesc(RelatedProvince::getCorrelationScore);
        return relatedProvinceMapper.selectList(wrapper);
    }
}
