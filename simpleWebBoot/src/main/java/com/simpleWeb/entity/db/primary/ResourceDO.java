package com.simpleWeb.entity.db.primary;

import lombok.Data;

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
}
