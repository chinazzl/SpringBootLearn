package com.simpleWeb.controller;

import com.simpleWeb.entity.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: zhang zhao lin
 * @Description:
 * @Date:Create：in 2020/12/29 20:51
 * @Modified By：
 */
@RestController
public class AlarmController {

    private static List<CmdbTotal> list = list = Arrays.asList(
            new CmdbTotal("100", "123", "hh", "aa", "2", "3", "1"),
            new CmdbTotal("101", "111", "333", "444", "5", "8", "2"),
            new CmdbTotal("100", "222", "hh", "aa", "2", "9", "1"),
            new CmdbTotal("101", "121", "333", "444", "5", "10", "2"),
            new CmdbTotal("102", "121", "333", "444", "5", "10", "3"),
            new CmdbTotal("102", "121", "333", "444", "5", "10", "3"),
            new CmdbTotal("103", "121", "333", "444", "5", "10", "4"),
            new CmdbTotal("103", "121", "333", "444", "5", "10", "4")
    );

    private static final int pageIndex = 2;

    private static final int pageSize = 2;


    @GetMapping("/alarm")
    public Result getTotalCmdb() {
        Map<String, List<CmdbTotal>> collect = Optional.ofNullable(list.stream().collect(Collectors.groupingBy(CmdbTotal::getAlarmId)))
                .orElseGet(HashMap::new);
//        List<FinalPO> rsList = new ArrayList<>();
        List<BaseTemp> rsList = new ArrayList<>();
        BaseTemp baseTemp;
        int rowno = getRowNo(pageIndex,pageSize);
        int dataCount = rowno;
        Iterator<Map.Entry<String, List<CmdbTotal>>> iterator = collect.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<CmdbTotal>> en = iterator.next();
            for (CmdbTotal t : list) {
                if (en.getKey().equals(t.getAlarmId())) {
                    baseTemp = new BaseTemp();
                    baseTemp.setRowno(rowno);
                    baseTemp.setAlaramId(t.getAlarmId());
                    baseTemp.setAppService(t.getAppService());
                    baseTemp.setSystemName(t.getSystemName());
                    baseTemp.setIpAddress(t.getIpAddress());
                    baseTemp.setTempList(collect.get(t.getAlarmId()));
                    rsList.add(baseTemp);
                    rowno++;
                    break;
                }

            }
        }
        rsList = rsList.stream().filter(t ->t.getRowno()>= dataCount && t.getRowno() < dataCount + pageSize).collect(Collectors.toList());
        return Result.ok(true, rsList);
    }

    private int getRowNo(int pageIndex, int pageSize) {
        return (pageIndex - 1) * pageSize + 1;
    }

}
