package com.simpleWeb.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 巡检结果返回VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionResultVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 解析是否成功
     */
    private Boolean success;
    
    /**
     * 错误信息
     */
    private String message;
    
    /**
     * 主机基本信息
     */
    private HostInfoVO hostInfo;
    
    /**
     * CPU使用率信息
     */
    private CpuUsageVO cpuUsage;
    
    /**
     * 内存使用率信息
     */
    private MemoryUsageVO memoryUsage;
    
    /**
     * Swap使用率信息
     */
    private SwapUsageVO swapUsage;
    
    /**
     * 文件系统使用率列表
     */
    private List<FilesystemUsageVO> filesystemUsages;
    
    /**
     * 高危文件系统列表（明细，正常时为空列表）
     */
    private List<FilesystemUsageVO> highRiskFilesystems;

    /**
     * 高危文件系统检测结果（含阈值区间与整体结论）
     */
    private HighRiskFilesystemVO highRiskFilesystemResult;
    
    /**
     * 关键服务运行状态列表
     */
    private List<ServiceStatusVO> serviceStatuses;
    
    /**
     * 主机信息VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HostInfoVO implements Serializable {
        private String hostIp;
        private String hostname;
        private String osVersion;
        private String kernelVersion;
        private String architecture;
        private Integer cpuCores;
        private String systemStartTime;
        private String uptime;
        private String inspectionTime;
        private String systemLoad;
    }
    
    /**
     * CPU使用率VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CpuUsageVO implements Serializable {
        private Integer cpuCores;
        private String systemLoad;
        private Double usagePercent;
        private String status;
        private Integer warningThresholdMin;
        private Integer warningThresholdMax;
    }
    
    /**
     * 内存使用率VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoryUsageVO implements Serializable {
        private Integer totalMb;
        private Integer usedMb;
        private Integer availableMb;
        private Double usagePercent;
        private String status;
        private Integer warningThresholdMin;
        private Integer warningThresholdMax;
    }
    
    /**
     * Swap使用率VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SwapUsageVO implements Serializable {
        private Integer totalMb;
        private Integer usedMb;
        private Double usagePercent;
        private String status;
        private Integer warningThresholdMin;
        private Integer warningThresholdMax;
    }
    
    /**
     * 文件系统使用率VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilesystemUsageVO implements Serializable {
        private String mountPoint;
        private String fsType;
        private String totalSize;
        private String usedSize;
        private String availableSize;
        private Integer usagePercent;
        private String status;
        private Integer warningThresholdMin;
        private Integer warningThresholdMax;
    }
    
    /**
     * 高危文件系统检测结果VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HighRiskFilesystemVO implements Serializable {
        private Integer warningThresholdMin;
        private Integer warningThresholdMax;
        /** 正常 / 异常 */
        private String status;
        /** 原始结论文本 */
        private String message;
        private List<FilesystemUsageVO> items;
    }

    /**
     * 服务运行状态VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceStatusVO implements Serializable {
        private String serviceName;
        private String status;
        private String pid;
    }
}
