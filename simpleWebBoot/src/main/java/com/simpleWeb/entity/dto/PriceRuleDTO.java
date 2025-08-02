package com.simpleWeb.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author: zhaolin
 * @Date: 2025/8/2
 * @Description:
 **/
@Data
@NoArgsConstructor
public class PriceRuleDTO {

    private String areaName;
    private Long areaId;
    private BigDecimal cpuX86Price;
    private BigDecimal cpuARMPrice;
    private BigDecimal cpuC86Price;
    private BigDecimal memoryPriice;
    private BigDecimal localDiskPrice;
    private BigDecimal nasDiskPrice;

    public PriceRuleDTO(Long areaId) {
        this.areaId = areaId;
        this.cpuX86Price = BigDecimal.ZERO;
        this.cpuARMPrice = BigDecimal.ZERO;
        this.cpuC86Price = BigDecimal.ZERO;
    }
}
