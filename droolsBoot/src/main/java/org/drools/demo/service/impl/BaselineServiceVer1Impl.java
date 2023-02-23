package org.drools.demo.service.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2023/2/15 15:27
 * @Description:
 */
@Service("baslineVer1")
public class BaselineServiceVer1Impl implements IBaselineService {

    @Autowired
    private IBaselineDao baselineDao;

    /**
     * {
     * "xAxis": [
     * "xxx",
     * "xxx"
     * ],
     * "series": {
     * "upTline": [],
     * "downTline": [],
     * "upBaseline": [],
     * "downBaseline": []
     * }
     * }
     *
     * @param ip
     * @return
     */
    @Override
    public BaselineBO getCpuBaseline(String ip) {
        BaselineBO baselineBO = new BaselineBO();
        BaselineBO.Series series = baselineBO.new Series();
        List<Double> upBaselines = new ArrayList<>();
        List<Double> downBaselines = new ArrayList<>();
        List<Double> upTlines = new ArrayList<>();
        List<Double> downTlines = new ArrayList<>();
        try {
            List<String> timePeriods = baselineDao.getTimePeriod(ip);
            for (String timePeriod : timePeriods) {
                List<BlueOsData> performanceDatas = baselineDao.getPerformanceData(ip, timePeriod);
                putNumberIntoRanged(performanceDatas, upBaselines, downBaselines, upTlines, downTlines);
            }
            System.out.println("=========基线计算完毕，等待画图......=============");
            baselineBO.setxAxis(timePeriods);
            series.setUpBaseline(upBaselines);
            series.setDownBaseline(downBaselines);
            series.setUpTline(upTlines);
            series.setDownTline(downTlines);
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
        Multimap<String, Double> rangeMap = ArrayListMultimap.create();
        for (Double cpu : cpuDatas) {
            int start = 0, rangeName = 1;
            double rangeStart = 0.0;
            while (rangeStart < Math.ceil(maxCPU)) {
                if (cpu >= rangeStart + 0.01 && cpu <= (int) (rangeStart + v)) {
                    rangeMap.put(String.valueOf(rangeName), cpu);
                    break;
                }
                rangeStart = rangeStart + v;
                rangeName++;
            }
        }
        System.out.println("分组后的数据" + rangeMap);
        // 获取样本数据，取分布不同区间数据量最多的前三个区间数据作为样本区间
        // rangeMap.asMap().entrySet().stream().map(m -> m.getValue()).filter(c -> c.size() <= 1);
        //List<Double> collect = rangeMap.asMap().entrySet().stream().map(m -> m.getValue())
        //        // .sorted(Comparator.comparingInt(Collection::size))
        //        .sorted((m1, m2) -> m2.stream().max(Double::compareTo).get().compareTo(m1.stream().max(Double::compareTo).get()))
        //        .limit((int) Math.floor(rangeMap.asMap().size() >> 1))
        //        .flatMap(Collection::stream)
        //        .collect(Collectors.toList());
        LinkedList<Collection<Double>> collect = rangeMap.asMap().entrySet().stream().map(m -> m.getValue())
                .sorted((m1, m2) -> Integer.compare(m2.size(), m1.size()))
                .collect(Collectors.toCollection(LinkedList::new));
        System.out.println(collect);
        Iterator<Collection<Double>> iterator = collect.iterator();
        List<Collection<Double>> useList = new ArrayList<Collection<Double>>();
        while (iterator.hasNext()) {
            Collection<Double> next = iterator.next();
            if (next.size() == 1) {
                break;
            }
            iterator.remove();
            useList.add(next);
        }
        List<Double> collect1 = collect.stream().flatMap(Collection::stream).collect(Collectors.toList());
        // 移除不需要的样本数据，在这里只需要后三个的区间的数据作为样本数据
        cpuDatas.removeAll(collect1);
        // 如果性能数据均匀分散到各个区间，则进行拆分然后将拆分剩余的数据进行移除。
        if (useList.size() != 1 && useList.size() > (int) Math.floor(rangeMap.asMap().size() >> 1)) {
            List<Double> collect2 = useList.stream()
                    .sorted((m1, m2) -> Integer.compare(m2.size(), m1.size()))
                    .skip((int) Math.floor(rangeMap.asMap().size() >> 1))
                    .flatMap(Collection::stream).collect(Collectors.toList());
            // 移除不需要的样本数据，在这里只需要后三个的区间的数据作为样本数据
            cpuDatas.removeAll(collect2);
        }
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
}
