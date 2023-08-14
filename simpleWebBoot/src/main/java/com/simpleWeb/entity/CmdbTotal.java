package com.simpleWeb.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhang zhao lin
 * @Description:
 * @Date:Create：in 2020/12/29 20:55
 * @Modified By：
 */
public class CmdbTotal {

    private String alarmId;

    private String error_id;

    private String appService;

    private String systemName;

    private String ipAddress;

    private String kpiName;

    private String magicType;


    public CmdbTotal(String alarmId, String error_id, String appService, String systemName, String ipAddress, String kpiName, String magicType) {
        this.alarmId = alarmId;
        this.error_id = error_id;
        this.appService = appService;
        this.systemName = systemName;
        this.ipAddress = ipAddress;
        this.kpiName = kpiName;
        this.magicType = magicType;
    }

    public CmdbTotal(String error_id, String kpiName, String magicType) {
        this.error_id = error_id;
        this.kpiName = kpiName;
        this.magicType = magicType;
    }

    public String getAppService() {
        return appService;
    }

    public void setAppService(String appService) {
        this.appService = appService;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }


    public String getError_id() {
        return error_id;
    }

    public void setError_id(String error_id) {
        this.error_id = error_id;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    public String getMagicType() {
        return magicType;
    }

    public void setMagicType(String magicType) {
        this.magicType = magicType;
    }

}
