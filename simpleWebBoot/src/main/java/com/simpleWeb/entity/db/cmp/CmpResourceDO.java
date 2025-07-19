package com.simpleWeb.entity.db.cmp;

import lombok.Data;

import java.util.Date;

/**
 * @author: zhaolin
 * @Date: 2025/7/17
 * @Description:
 **/
@Data
public class CmpResourceDO {

    private Long id;
    private String serviceType;
    private String cpuArchitecture;
    private int cpuCore;
    private int memory;
    private int localDisk;
    private int shareDisk;
    private Date startTime;
    private Date endTime;
}
