package com.stock.investment.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stock.investment.entity.StockComment;
import com.stock.investment.entity.StockPost;
import com.stock.investment.repository.StockCommentMapper;
import com.stock.investment.repository.StockPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class StockBarService {

    @Autowired
    private StockPostMapper stockPostMapper;

    @Autowired
    private StockCommentMapper stockCommentMapper;

    public Page<StockPost> getPostList(int page, int size) {
        Page<StockPost> pageParam = new Page<>(page, size);
        return stockPostMapper.selectPage(pageParam, new QueryWrapper<StockPost>().orderByDesc("create_time"));
    }

    public StockPost getPostDetail(Long id) {
        return stockPostMapper.selectById(id);
    }

    public void createPost(StockPost post) {
        post.setCreateTime(LocalDateTime.now());
        post.setLikeCount(0);
        post.setViewCount(0);
        stockPostMapper.insert(post);
    }

    public List<StockComment> getComments(Long postId) {
        return stockCommentMapper.selectList(new QueryWrapper<StockComment>().eq("post_id", postId).orderByDesc("create_time"));
    }

    public void createComment(StockComment comment) {
        comment.setCreateTime(LocalDateTime.now());
        comment.setLikeCount(0);
        stockCommentMapper.insert(comment);
    }
    
    public List<Map<String, Object>> getHotStocks() {
        // 模拟热门股票数据
        List<Map<String, Object>> hotStocks = new ArrayList<>();
        hotStocks.add(createStock("贵州茅台", "600519", 1750.00, 2.5));
        hotStocks.add(createStock("宁德时代", "300750", 220.50, -1.2));
        hotStocks.add(createStock("比亚迪", "002594", 260.80, 1.8));
        hotStocks.add(createStock("五粮液", "000858", 160.20, 0.5));
        hotStocks.add(createStock("招商银行", "600036", 32.50, -0.3));
        return hotStocks;
    }

    private Map<String, Object> createStock(String name, String code, double price, double change) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("code", code);
        map.put("price", price);
        map.put("change", change);
        return map;
    }
}
