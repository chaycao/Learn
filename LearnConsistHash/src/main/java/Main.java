import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.*;

public class Main {

    private static int dataNum = 10000000;

    private static int nodeNum = 100;

    private static int vNodeNum = 100;

    private static int newNodeNum = 110;

    private static List<Integer> datas = initData(dataNum);

    private static List<String> nodes = initNode(nodeNum);

    private static List<String> newNodes = initNode(newNodeNum);

    public static void normalHashMain() {
        Map<String, Integer> nodeCount = new HashMap<>();
        for (Integer data : datas) {
            String node = normalHash(data, nodes);

            if (nodeCount.containsKey(node)) {
                nodeCount.put(node, nodeCount.get(node) + 1);
            } else {
                nodeCount.put(node, 1);
            }
        }

        analyze(nodeCount, dataNum, nodeNum);
    }

    public static void normalHashMigrateMain() {
        int migrateCount = 0;
        for (Integer data : datas) {
            String node = normalHash(data, nodes);
            String newNode = normalHash(data, newNodes);
            if (!node.equals(newNode)) {
                migrateCount++;
            }
        }
        System.out.println(String.format("数据迁移量：%d（%.2f%%）", migrateCount, migrateCount * 100.0 / datas.size()));
    }

    public static String normalHash(Integer data, List<String> nodes) {
        int hash = hash(data);
        int nodeIndex = hash % nodes.size();
        return nodes.get(nodeIndex);
    }

    public static void consistHashMain() {
        Map<String, Integer> nodeCount = new HashMap<>();
        SortedMap<Integer, String> circle = new TreeMap<>();
        for (String node : nodes) {
            circle.put(hash(node), node);
        }

        for (Integer data : datas) {
            String node = consistHash(data, circle);
            if (nodeCount.containsKey(node)) {
                nodeCount.put(node, nodeCount.get(node) + 1);
            } else {
                nodeCount.put(node, 1);
            }
        }

        analyze(nodeCount, dataNum, nodeNum);
    }

    public static void consistHashMigrateMain() {
        int migrateCount = 0;
        SortedMap<Integer, String> circle = new TreeMap<>();
        for (String node : nodes) {
            circle.put(hash(node), node);
        }
        SortedMap<Integer, String> newCircle = new TreeMap<>();
        for (String node : newNodes) {
            newCircle.put(hash(node), node);
        }

        for (Integer data : datas) {
            String node = consistHash(data, circle);
            String newNode = consistHash(data, newCircle);
            if (!node.equals(newNode)) {
                migrateCount++;
            }
        }
        System.out.println(String.format("数据迁移量：%d（%.2f%%）", migrateCount, migrateCount * 100.0 / datas.size()));
    }

    public static String consistHash(Integer data, SortedMap<Integer, String> circle) {
        int hash = hash(data);
        // 从环中取大于等于hash值的部分
        SortedMap<Integer, String> subCircle = circle.tailMap(hash);
        int index;
        // 如果在大于等于hash值的部分没有节点，则取环开始的第一个节点
        if (subCircle.isEmpty()) {
            index = circle.firstKey();
        } else {
            index = subCircle.firstKey();
        }
        return circle.get(index);
    }

    public static void consistHashVirtualNodeMain() {
        Map<String, Integer> nodeCount = new HashMap<>();
        SortedMap<Integer, String> circle = new TreeMap<>();
        for (String node : nodes) {
            for (int i = 0; i < vNodeNum; i++) {
                circle.put(hash(node + "-" + i), node);
            }
        }

        for (Integer data : datas) {
            String node = consistHashVirtualNode(data, circle);

            if (nodeCount.containsKey(node)) {
                nodeCount.put(node, nodeCount.get(node) + 1);
            } else {
                nodeCount.put(node, 1);
            }
        }

        analyze(nodeCount, dataNum, nodeNum);
    }

    public static void consistHashVirtualNodeMigrateMain() {
        int migrateCount = 0;
        SortedMap<Integer, String> circle = new TreeMap<>();
        for (String node : nodes) {
            circle.put(hash(node), node);
        }
        SortedMap<Integer, String> newCircle = new TreeMap<>();
        for (String node : newNodes) {
            newCircle.put(hash(node), node);
        }

        for (Integer data : datas) {
            String node = consistHash(data, circle);
            String newNode = consistHash(data, newCircle);
            if (!node.equals(newNode)) {
                migrateCount++;
            }
        }
        System.out.println(String.format("数据迁移量：%d（%.2f%%）", migrateCount, migrateCount * 100.0 / datas.size()));
    }


    private static String consistHashVirtualNode(Integer data, SortedMap<Integer, String> circle) {
        int hash = hash(data);
        // 从环中取大于等于hash值的部分
        SortedMap<Integer, String> subCircle = circle.tailMap(hash);
        int index;
        // 如果在大于等于hash值的部分没有节点，则取环开始的第一个节点
        if (subCircle.isEmpty()) {
            index = circle.firstKey();
        } else {
            index = subCircle.firstKey();
        }
        return circle.get(index);
    }

    public static void analyzeCircleNode() {
        List<Integer> hashs = new ArrayList<>();
        for (String node : nodes) {
            hashs.add(hash(node));
        }
        Collections.sort(hashs);
        List<Integer> gaps = new ArrayList<>();
        for (int i = 0; i < hashs.size() - 1; i++) {
            int a = hashs.get(i);
            int b = hashs.get(i + 1);
            gaps.add(b - a);
        }
        int last = Integer.MAX_VALUE - hashs.get(hashs.size() - 1) + hashs.get(0);
        gaps.add(last);

        IntSummaryStatistics s1 = gaps.stream().mapToInt(Integer::intValue).summaryStatistics();
        int max = s1.getMax();
        int min = s1.getMin();
        double average = s1.getAverage();
        int range = max - min;
        double standardDeviation
                = gaps.stream().mapToDouble(n -> Math.abs(n - average)).summaryStatistics().getAverage();

        System.out.println(String.format("平均值：%.2f", average));
        System.out.println(String.format("最大值：%d,（%.2f%%）", max, 100.0 * max / average));
        System.out.println(String.format("最小值：%d,（%.2f%%）", min, 100.0 * min / average));
        System.out.println(String.format("极差：%d,（%.2f%%）", range, 100.0 * range / average));
        System.out.println(String.format("标准差：%f,（%.2f%%）", standardDeviation, 100.0 * standardDeviation / average));
    }

    private static int hash(Object object) {
        HashFunction hashFunction = Hashing.murmur3_32();
        if (object instanceof Integer) {
            return Math.abs(hashFunction.hashInt((Integer) object).asInt());
        } else if (object instanceof String) {
            return Math.abs(hashFunction.hashUnencodedChars((String) object).asInt());
        }
        return -1;
    }


    private static List<Integer> initData(int n) {
        List<Integer> datas = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            datas.add(random.nextInt());
        }
        return datas;
    }

    private static List<String> initNode(int n) {
        List<String> nodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            nodes.add(String.format("192.168.1.%d", i));
        }
        return nodes;
    }

    public static void analyze(Map<String, Integer> nodeCount, int dataNum, int nodeNum) {
        double average = (double) dataNum / nodeNum;

        IntSummaryStatistics s1
                = nodeCount.values().stream().mapToInt(Integer::intValue).summaryStatistics();
        int max = s1.getMax();
        int min = s1.getMin();
        int range = max - min;
        double standardDeviation
                = nodeCount.values().stream().mapToDouble(n -> Math.abs(n - average)).summaryStatistics().getAverage();

        System.out.println(String.format("平均值：%.2f", average));
        System.out.println(String.format("最大值：%d,（%.2f%%）", max, 100.0 * max / average));
        System.out.println(String.format("最小值：%d,（%.2f%%）", min, 100.0 * min / average));
        System.out.println(String.format("极差：%d,（%.2f%%）", range, 100.0 * range / average));
        System.out.println(String.format("标准差：%.2f,（%.2f%%）", standardDeviation, 100.0 * standardDeviation / average));
    }


    public static void main(String[] args) {
        System.out.println("普通哈希：");
        normalHashMain();

        System.out.println("\n普通哈希迁移：");
        normalHashMigrateMain();

        System.out.println("\n一致性哈希：");
        consistHashMain();

        System.out.println("\n一致性哈希迁移：");
        consistHashMigrateMain();

        System.out.println("\n一致性哈希+虚拟节点：");
        consistHashVirtualNodeMain();

        System.out.println("\n一致性哈希+虚拟节点迁移：");
        consistHashVirtualNodeMigrateMain();

//        analyzeCircleNode();
    }
}
