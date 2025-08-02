package com.simpleWeb.entity.db.primary;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author: zhaolin
 * @Date: 2025/7/26
 * @Description:
 **/
@Data
public class ResourcePriceDO {

    private BigDecimal cpuX86;
    private BigDecimal cpuC86;
    private BigDecimal cpuARM;
    private BigDecimal memoryPrice;
    private BigDecimal nasPrice;
    private BigDecimal localDiskPrice;
    private BigDecimal areaId;
    private String areaName;
    private LocalDate collectDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
