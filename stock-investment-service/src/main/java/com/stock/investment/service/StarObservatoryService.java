package com.stock.investment.service;

import com.stock.investment.entity.StarGraphData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StarObservatoryService {

    public StarGraphData getGraphData() {
        StarGraphData data = new StarGraphData();
        List<StarGraphData.Node> nodes = new ArrayList<>();
        List<StarGraphData.Link> links = new ArrayList<>();

        // 模拟数据：新能源汽车产业链
        // 核心节点 (Category 0)
        addNode(nodes, "CATL", "宁德时代", "300750", 100, 0);
        addNode(nodes, "BYD", "比亚迪", "002594", 95, 0);

        // 关联节点 - 上游原材料 (Category 1)
        addNode(nodes, "Ganfeng", "赣锋锂业", "002460", 60, 1);
        addNode(nodes, "Tianqi", "天齐锂业", "002466", 55, 1);
        addNode(nodes, "Huayou", "华友钴业", "603799", 58, 1);
        addNode(nodes, "Semcorp", "恩捷股份", "002812", 65, 1);
        
        // 关联节点 - 下游整车厂 (Category 1)
        addNode(nodes, "Tesla", "特斯拉", "TSLA", 85, 1);
        addNode(nodes, "NIO", "蔚来", "NIO", 50, 1);
        addNode(nodes, "Xpeng", "小鹏汽车", "XPEV", 45, 1);
        addNode(nodes, "LiAuto", "理想汽车", "LI", 48, 1);

        // 关系构建
        // 上游 -> 核心
        addLink(links, "Ganfeng", "CATL", "锂供应");
        addLink(links, "Tianqi", "CATL", "锂供应");
        addLink(links, "Huayou", "CATL", "钴/前驱体");
        addLink(links, "Semcorp", "CATL", "隔膜供应");
        
        addLink(links, "Ganfeng", "BYD", "锂供应");
        addLink(links, "Semcorp", "BYD", "隔膜供应");
        
        // 核心 -> 下游
        addLink(links, "CATL", "Tesla", "动力电池主供");
        addLink(links, "CATL", "NIO", "全系电池供应");
        addLink(links, "CATL", "Xpeng", "电池供应");
        addLink(links, "CATL", "LiAuto", "电池供应");
        
        addLink(links, "BYD", "Tesla", "刀片电池供应");

        data.setNodes(nodes);
        data.setLinks(links);
        return data;
    }

    public String analyzeRelations() {
        return "### 产业链智能分析报告\n\n" +
               "**Q: 当前图谱展示了什么核心逻辑？**\n" +
               "A: 图谱展示了以**宁德时代**和**比亚迪**为双核心的新能源汽车产业链生态。通过10个关键节点的连接，清晰呈现了从上游资源（锂、钴、隔膜）到中游电池制造，再到下游整车终端（特斯拉、蔚小理）的完整传导路径。\n\n" +
               "**Q: 为什么恩捷股份和华友钴业被纳入图谱？**\n" +
               "A: **恩捷股份**作为隔膜龙头，同时向宁德时代和比亚迪供货，掌握着电池安全的关键材料话语权；**华友钴业**则是宁德时代重要的正极材料前驱体供应商，体现了产业链对稀缺资源的深度绑定。\n\n" +
               "**Q: 宁德时代与造车新势力的关系如何？**\n" +
               "A: 宁德时代几乎垄断了蔚来、理想、小鹏的主力车型电池供应。这种深度绑定关系使得宁德时代在产业链中拥有极强的议价能力，但同时也面临车企自研电池（如蔚来）的潜在挑战。\n\n" +
               "**Q: 投资者应该关注哪些风险点？**\n" +
               "A: 1. **上游价格波动**：碳酸锂价格的剧烈波动会直接冲击中游利润。\n" +
               "   2. **技术路线变更**：固态电池的商业化进程可能重塑现有的隔膜和电解液格局。\n" +
               "   3. **产能过剩**：中游电池环节已出现结构性产能过剩迹象。";
    }

    public String chat(String question) {
        if (question.contains("宁德时代") || question.contains("CATL")) {
            return "宁德时代 (300750) 是全球动力电池龙头。在图谱中，它处于绝对枢纽位置，上游绑定赣锋、华友等资源方，下游几乎覆盖所有主流车企。其核心壁垒在于极致的成本控制和技术迭代能力（如麒麟电池）。";
        } else if (question.contains("比亚迪") || question.contains("BYD")) {
            return "比亚迪 (002594) 具备全产业链垂直整合能力。与宁德时代不同，它既是电池厂也是整车厂。图谱显示它开始向特斯拉外供刀片电池，标志着其电池业务外供战略的重大突破。";
        } else if (question.contains("理想") || question.contains("Li")) {
            return "理想汽车 (LI) 是增程式电动车的代表。图谱显示其电池主要由宁德时代供应，但近期也在引入欣旺达等二供以降低成本。";
        } else if (question.contains("恩捷") || question.contains("隔膜")) {
            return "恩捷股份 (002812) 是湿法隔膜全球龙头。它同时连接了宁德时代和比亚迪两大巨头，在产业链中具有极高的话语权，毛利率长期维持在较高水平。";
        } else if (question.contains("华友") || question.contains("钴")) {
            return "华友钴业 (603799) 转型为锂电材料一体化巨头。它为宁德时代提供关键的钴资源和三元前驱体，是保障电池供应链安全的重要一环。";
        } else if (question.contains("风险")) {
            return "当前主要风险包括：1. 碳酸锂价格波动导致的库存减值风险；2. 欧美《通胀削减法案》对供应链出海的限制；3. 下游车企价格战对上游的降本压力传导。";
        } else {
            return "这是一个深刻的问题。从图谱来看，这10家企业构成了紧密的利益共同体。您可以尝试询问具体企业的关系，例如“宁德时代和特斯拉”或“恩捷股份的作用”。";
        }
    }

    private void addNode(List<StarGraphData.Node> nodes, String id, String name, String symbol, double value, int category) {
        StarGraphData.Node node = new StarGraphData.Node();
        node.setId(id);
        node.setName(name);
        node.setSymbol(symbol);
        node.setValue(value);
        node.setCategory(category);
        nodes.add(node);
    }

    private void addLink(List<StarGraphData.Link> links, String source, String target, String relation) {
        StarGraphData.Link link = new StarGraphData.Link();
        link.setSource(source);
        link.setTarget(target);
        link.setRelation(relation);
        links.add(link);
    }
}
