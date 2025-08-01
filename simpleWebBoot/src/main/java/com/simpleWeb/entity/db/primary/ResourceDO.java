package com.simpleWeb.entity.db.primary;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author: zhaolin
 * @Date: 2025/7/17
 * @Description:
 **/
@Data
public class ResourceDO {

    private Long id;
    private String serviceType;
    private String cpuArchitecture;
    private int cpuCore;
    private int memory;
    private int localDisk;
    private int shareDisk;
    private String areaName;
    private Long areaId;
    private String appSystemName;
    private String appSystemId;
    private LocalDate createDate;
}
