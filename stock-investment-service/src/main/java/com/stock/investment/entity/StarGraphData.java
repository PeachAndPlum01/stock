package com.stock.investment.entity;

import lombok.Data;
import java.util.List;

@Data
public class StarGraphData {
    private List<Node> nodes;
    private List<Link> links;

    @Data
    public static class Node {
        private String id;
        private String name;
        private String symbol;
        private double value;
        private int category; // 0: 核心, 1: 关联
    }

    @Data
    public static class Link {
        private String source;
        private String target;
        private String relation;
    }
}
