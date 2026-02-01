package com.stock.investment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stock.investment.entity.InvestmentInfo;
import com.stock.investment.mapper.InvestmentInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 投资信息服务
 */
@Service
public class InvestmentInfoService {

    @Autowired
    private InvestmentInfoMapper investmentInfoMapper;

    /**
     * 分页查询股票信息
     */
    public IPage<InvestmentInfo> getInvestmentList(int page, int size, String province, 
                                                     String keyword, String sortBy, String sortOrder) {
        Page<InvestmentInfo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<InvestmentInfo> wrapper = new LambdaQueryWrapper<>();
        
        // 状态过滤
        wrapper.eq(InvestmentInfo::getStatus, 1);
        
        // 省份过滤
        if (StringUtils.hasText(province)) {
            wrapper.eq(InvestmentInfo::getProvince, province);
        }
        
        // 关键词搜索（公司名称或题材）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(InvestmentInfo::getCompanyName, keyword)
                             .or()
                             .like(InvestmentInfo::getTitle, keyword));
        }
        
        // 排序
        if (StringUtils.hasText(sortBy)) {
            boolean isAsc = "asc".equalsIgnoreCase(sortOrder);
            switch (sortBy) {
                case "tenDayChange":
                    wrapper.orderBy(true, isAsc, InvestmentInfo::getTenDayChange);
                    break;
                case "oneDayChange":
                    wrapper.orderBy(true, isAsc, InvestmentInfo::getOneDayChange);
                    break;
                case "investmentAmount":
                    wrapper.orderBy(true, isAsc, InvestmentInfo::getInvestmentAmount);
                    break;
                case "marketCap":
                    wrapper.orderBy(true, isAsc, InvestmentInfo::getMarketCap);
                    break;
                default:
                    wrapper.orderByDesc(InvestmentInfo::getTenDayChange);
            }
        } else {
            wrapper.orderByDesc(InvestmentInfo::getTenDayChange);
        }
        
        return investmentInfoMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 根据ID获取股票详情
     */
    public InvestmentInfo getInvestmentById(Long id) {
        return investmentInfoMapper.selectById(id);
    }

    /**
     * 按省份统计股票数量
     */
    public List<Map<String, Object>> countByProvince() {
        return investmentInfoMapper.countByProvince();
    }

    /**
     * 获取涨幅前N的股票
     */
    public List<InvestmentInfo> getTopGainers(int limit) {
        return investmentInfoMapper.getTopGainers(limit);
    }

    /**
     * 按省份获取涨幅前N的股票
     */
    public List<InvestmentInfo> getTopGainersByProvince(String province, int limit) {
        return investmentInfoMapper.getTopGainersByProvince(province, limit);
    }

    /**
     * 按涨幅范围查询
     */
    public List<InvestmentInfo> getByChangeRange(BigDecimal minChange, BigDecimal maxChange) {
        LambdaQueryWrapper<InvestmentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvestmentInfo::getStatus, 1);
        
        if (minChange != null) {
            wrapper.ge(InvestmentInfo::getTenDayChange, minChange);
        }
        if (maxChange != null) {
            wrapper.le(InvestmentInfo::getTenDayChange, maxChange);
        }
        
        wrapper.orderByDesc(InvestmentInfo::getTenDayChange);
        return investmentInfoMapper.selectList(wrapper);
    }

    /**
     * 按题材查询
     */
    public List<InvestmentInfo> getByTheme(String theme) {
        LambdaQueryWrapper<InvestmentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvestmentInfo::getStatus, 1);
        wrapper.like(InvestmentInfo::getTitle, theme);
        wrapper.orderByDesc(InvestmentInfo::getTenDayChange);
        return investmentInfoMapper.selectList(wrapper);
    }

    /**
     * 按日期查询
     */
    public List<InvestmentInfo> getByDate(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<InvestmentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvestmentInfo::getStatus, 1);
        
        if (startDate != null) {
            wrapper.ge(InvestmentInfo::getInvestmentDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(InvestmentInfo::getInvestmentDate, endDate);
        }
        
        wrapper.orderByDesc(InvestmentInfo::getInvestmentDate);
        return investmentInfoMapper.selectList(wrapper);
    }

    /**
     * 获取所有省份列表
     */
    public List<String> getAllProvinces() {
        LambdaQueryWrapper<InvestmentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(InvestmentInfo::getProvince);
        wrapper.eq(InvestmentInfo::getStatus, 1);
        wrapper.groupBy(InvestmentInfo::getProvince);
        
        return investmentInfoMapper.selectList(wrapper).stream()
                .map(InvestmentInfo::getProvince)
                .distinct()
                .toList();
    }
}
