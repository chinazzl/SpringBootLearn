package com.simpleWeb.entity.db.primary;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author: zhaolin
 * @Date: 2025/7/26
 * @Description:
 **/
@Data
public class ResourcePriceDO {

    private int cpuX86;
    private int cpuC86;
    private int cpuARM;
    private int memoryPrice;
    private int nasPrice;
    private int localDiskPrice;
    private int areaId;
    private String areaName;
    private LocalDate collectDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
