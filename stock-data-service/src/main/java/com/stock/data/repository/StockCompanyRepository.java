package com.stock.data.repository;

import com.stock.data.model.StockCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 股票公司信息Repository
 */
@Repository
public interface StockCompanyRepository extends JpaRepository<StockCompany, Long> {

    /**
     * 根据股票代码查询公司信息
     *
     * @param tsCode 股票代码
     * @return 公司信息
     */
    Optional<StockCompany> findByTsCode(String tsCode);

    /**
     * 根据公司名称查询公司信息（模糊查询）
     *
     * @param comName 公司名称
     * @return 公司信息列表
     */
    List<StockCompany> findByComNameContaining(String comName);

    /**
     * 根据城市查询公司信息
     *
     * @param city 城市
     * @return 公司信息列表
     */
    List<StockCompany> findByCity(String city);

    /**
     * 根据省份查询公司信息
     *
     * @param province 省份
     * @return 公司信息列表
     */
    List<StockCompany> findByProvince(String province);

    /**
     * 根据交易所查询公司信息
     *
     * @param exchange 交易所
     * @return 公司信息列表
     */
    List<StockCompany> findByExchange(String exchange);

    /**
     * 检查股票代码是否存在
     *
     * @param tsCode 股票代码
     * @return 是否存在
     */
    boolean existsByTsCode(String tsCode);

    /**
     * 删除指定股票代码的公司信息
     *
     * @param tsCode 股票代码
     */
    void deleteByTsCode(String tsCode);

    /**
     * 统计公司总数
     *
     * @return 公司总数
     */
    @Query("SELECT COUNT(s) FROM StockCompany s")
    long countTotal();

    /**
     * 根据员工人数范围查询
     *
     * @param minEmployees 最小员工数
     * @param maxEmployees 最大员工数
     * @return 公司信息列表
     */
    @Query("SELECT s FROM StockCompany s WHERE s.employees BETWEEN :minEmployees AND :maxEmployees")
    List<StockCompany> findByEmployeesRange(@Param("minEmployees") Integer minEmployees,
                                            @Param("maxEmployees") Integer maxEmployees);
}
