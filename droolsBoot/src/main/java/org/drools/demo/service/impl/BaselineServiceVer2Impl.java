package org.drools.demo.service.impl;

import org.drools.demo.Entity.bo.BaselineBO;
import org.drools.demo.Entity.po.BlueOsData;
import org.drools.demo.dao.IBaselineDao;
import org.drools.demo.service.IBaselineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2023/2/23 10:50
 * @Description:
 */
@Service("baslineVer2")
public class BaselineServiceVer2Impl implements IBaselineService {

    @Autowired
    private IBaselineDao baselineDao;

    @Override
    public BaselineBO getCpuBaseline(String ip) {
        BaselineBO baselineBO = new BaselineBO();
        BaselineBO.Series series = baselineBO.new Series();
        List<Double> upBaselines = new ArrayList<>();
        List<Double> downBaselines = new ArrayList<>();
        List<Double> upTlines = new ArrayList<>();
        List<Double> downTlines = new ArrayList<>();
        // List<Double> realCpus = new ArrayList<>();
        try {
            List<String> timePeriods = baselineDao.getTimePeriod(ip);
            // 获取真实cpu使用率
            List<String> realCpusInfos = baselineDao.getRealCpus(ip);
            for (String timePeriod : timePeriods) {
                List<BlueOsData> performanceDatas = baselineDao.getPerformanceData(ip, timePeriod);
                System.out.printf("%s，timePeriod：%s",ip,timePeriod );
                putNumberIntoRanged(performanceDatas, upBaselines, downBaselines, upTlines, downTlines);
            }
            System.out.println("=========基线计算完毕，等待画图......=============");
            baselineBO.setxAxis(timePeriods);
            series.setUpBaseline(upBaselines);
            series.setDownBaseline(downBaselines);
            series.setUpTline(upTlines);
            series.setDownTline(downTlines);
            series.setRealCpu(realCpusInfos);
            baselineBO.setSeries(series);
            System.out.println("=========基线画图完毕=============");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return baselineBO;
    }

    /**
     * 计算基线值
     * 1. 获取一个ip一个月内每天相同时间的性能数据
     * 2. 性能数据按照区间分布进行数据分区
     * 3. 分区数据比对，去除噪声，获得样本数据
     * 4. 根据设置的置信度，获取准滑动窗口采纳的数据
     * 5. 根据滑动窗口算法进行汇总数据并计算每个汇总区间的标准差
     * 6. 取出标准差最小的区间集合作为当前时间的基线标准。
     */
    public void putNumberIntoRanged(List<BlueOsData> blueOsDatas, List<Double> upBaselines, List<Double> downBaselines,
                                    List<Double> upTline, List<Double> downTline) {
        // 采集一个ip 一个月内的当前时间同一时刻的cpu使用率的性能数据。（例子）
        // Double[] doubles = new Double[]{9.18, 9.64, 8.53, 9.25, 9.01, 9.35, 9.48, 6.62, 6.26, 5.37, 6.97, 9.02, 7.13, 6.57, 9.03,
        //         9.65, 9.50, 9.91, 7.24, 5.65, 7.72, 9.73, 6.98, 9.94, 8.36, 6.66, 8.93, 5.90, 9.31, 7.20, 0.12, 2.32};
        List<Double> cpuDatas = blueOsDatas.stream().map(BlueOsData::getTotalUsage).collect(Collectors.toList());
        // 获取这段时间性能数据的最大值
        Double maxCPU = cpuDatas.stream().max(Double::compareTo).get();
        // 根据样本最大值计算五个区间范围
        long v = Math.round((maxCPU / 5)) == 0 ? 1 : Math.round((maxCPU / 5));
        // 将时间段的性能数据分别分布在不同的区间[0,0+v], [0+v+0.01, 0+v+0.01+v] ...
        List<Double> sorted = cpuDatas.stream().sorted(Double::compareTo).collect(Collectors.toList());
        List<Double[]> container = new ArrayList<Double[]>();
        // 区间取数法
        ranger(container, sorted, v, 1, v);
        System.out.println("分组后的数据（排序前）: ");
        // =================================================================
        for (Double[] ds : container) {
            for (Double d : ds) {
                System.out.print(d + ",");
            }
            System.out.println();
        }
        // =================================================================
        LinkedList<Double[]> collect = container.stream()
                .sorted(Comparator.comparingInt(m -> m.length))
                .collect(Collectors.toCollection(LinkedList::new));
        System.out.println("分组后的数据（排序后）: ");
        // =================================================================
        for (Double[] ds : collect) {
            for (Double d : ds) {
                System.out.print(d + ",");
            }
            System.out.println();
        }
        // =================================================================
        List<Double[]> useList = new ArrayList<Double[]>();
        // 计算中位数
        double middleNum = 0;
        if (collect.size() % 2 == 0) {
            middleNum = Math.ceil((collect.get((collect.size()-1)/2).length + collect.get((collect.size() / 2)).length) / 2.0);
        } else {
            if (collect.size() == 1) {
                middleNum = 1;
            } else {
                middleNum = Math.ceil((collect.get((collect.size() / 2 )).length) / 2.0);
            }
        }
        System.out.printf("中位数为：%f \n",middleNum);
        // 过滤小于中位数的集合。
        for (Double[] ds : collect) {
            if (ds.length < middleNum) {
                useList.add(ds);
            }
        }
        // =================================================================
        System.out.println("需要删除小于中位数的样本数：");
        for (Double[] ds : useList) {
            for (Double d : ds) {
                System.out.print(d + ",");
            }
            System.out.println();
        }
        // =================================================================
        cpuDatas.removeAll(useList.stream().flatMap(Stream::of).collect(Collectors.toList()));
        System.out.println("过滤后的数据：" + cpuDatas);
        // 滑动窗口，分别计算1~24  2~25 3~ 26 以此类推的标准差
        // 开始计算标准差
        // Double reduce = window.stream().reduce(0.0, Double::sum);
        // 设置置信度为0.8
        double confidence = 0.8;
        // 获取滑动窗口的数据采纳个数
        double sampleSize = cpuDatas.size() * confidence;
        // 滑动窗口的计算后的数据集合
        List<Double[]> windowsData = slidingWindows(cpuDatas.toArray(new Double[0]), (int) sampleSize);
        List<Double> deviations = new ArrayList<Double>();
        for (Double[] window : windowsData) {
            // 计算平均数
            double avg = (Arrays.stream(window).reduce(Double::sum).orElse(0.0)) / sampleSize;
            // 保留三位小数
            BigDecimal b = new BigDecimal(String.valueOf(avg));
            double round_avg = b.setScale(4, RoundingMode.HALF_UP).doubleValue();
            double sum = 0.0;
            for (Double d : window) {
                sum += roundDouble(Math.pow((d - round_avg), 2), 3);
            }
            // 获取每一个区间的标准差
            double deviation = Math.sqrt(sum / (window.length - 1));
            deviations.add(deviation);
        }
        // 取出最小的标准差作为当前时间的基线标准。
        Integer baseline_Index = deviations.stream().min(Double::compareTo).map(d -> deviations.indexOf(d)).get();
        Double baseline_Down = Arrays.stream(windowsData.get(baseline_Index)).min(Double::compareTo).get();
        Double baseline_Up = Arrays.stream(windowsData.get(baseline_Index)).max(Double::compareTo).get();
        Double up_Tline = baseline_Up * (1 + 0.3);
        Double down_Tline = baseline_Down * (1 - 0.2);
        System.out.println("下基线是：" + baseline_Down + "，上基线是：" + baseline_Up + "，上容忍线：" + up_Tline + "，下容忍线：" + down_Tline);
        upBaselines.add(baseline_Up);
        downBaselines.add(baseline_Down);
        upTline.add(up_Tline);
        downTline.add(down_Tline);
    }

    /**
     * 滑动窗口计算每k个数据之和
     *
     * @param nums
     * @param k
     * @return
     */
    private List<Double[]> slidingWindows(Double[] nums, int k) {
        int right = 0;
        // double[] res = new double[nums.length - k + 1];
        List<Double[]> resList = new ArrayList<Double[]>();
        int index = 0;
        LinkedList<Double> list = new LinkedList<>();
        // 开始构造窗口
        while (right < nums.length) {
            // 不断添加
            list.addLast(nums[right]);
            right++;
            // 构造完成，这时候需要根据条件做一些操作
            if (right >= k) {
                // res[index++]= list.peekFirst();
                resList.add(list.toArray(new Double[0]));
                // 如果发现第一个已经在窗口外面则移除
                if (Objects.equals(list.peekFirst(), nums[right - k])) {
                    list.removeFirst();
                }
            }
        }
        return resList;
    }

    /**
     * 保留指定位数的小数
     *
     * @param val
     * @param scale
     * @return
     */
    private double roundDouble(double val, int scale) {
        BigDecimal b = new BigDecimal(String.valueOf(val));
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 区间取数算法
     *
     * @param container 存储结果的容器
     * @param ds        从小到大排序后的数据
     * @param k         递增步数
     * @param start     起始 0
     * @param close     闭区间临界值 k * start
     * @return
     */
    private void ranger(List<Double[]> container, List<Double> ds, long k, long start, long close) {
        Double[] d = new Double[ds.size()];
        Iterator<Double> iterator = ds.iterator();
        int i = 0;
        while (iterator.hasNext() && !ds.isEmpty()) {
            Double next = iterator.next();
            if (next > close) {
                ranger(container, ds, k, ++start, k * start);
            } else {
                d[i++] = next;
                iterator.remove();
            }
        }
        Double[] doubles = Stream.of(d).filter(Objects::nonNull).toArray(Double[]::new);
        if (doubles.length > 0) {
            container.add(doubles);
        }
    }
}
