package com.stock.investment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stock.investment.entity.StockComment;
import com.stock.investment.entity.StockPost;
import com.stock.investment.service.StockBarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/investment/bar")
public class StockBarController {

    @Autowired
    private StockBarService stockBarService;

    @GetMapping("/posts")
    public Page<StockPost> getPostList(@RequestParam(defaultValue = "1") int page, 
                                     @RequestParam(defaultValue = "10") int size) {
        return stockBarService.getPostList(page, size);
    }

    @PostMapping("/post")
    public void createPost(@RequestBody StockPost post) {
        // 简单模拟获取当前用户，实际应从Token解析
        if (post.getUserId() == null) {
            post.setUserId(1L);
            post.setUsername("User");
        }
        stockBarService.createPost(post);
    }

    @GetMapping("/post/{id}")
    public StockPost getPostDetail(@PathVariable Long id) {
        return stockBarService.getPostDetail(id);
    }

    @GetMapping("/post/{id}/comments")
    public List<StockComment> getComments(@PathVariable Long id) {
        return stockBarService.getComments(id);
    }

    @PostMapping("/comment")
    public void createComment(@RequestBody StockComment comment) {
        if (comment.getUserId() == null) {
            comment.setUserId(1L);
            comment.setUsername("User");
        }
        stockBarService.createComment(comment);
    }

    @GetMapping("/hot-stocks")
    public List<Map<String, Object>> getHotStocks() {
        return stockBarService.getHotStocks();
    }
}
