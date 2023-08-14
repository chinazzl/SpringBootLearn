package com.simpleWeb.entity;

import java.util.List;

/**
 * @Author: zhang zhao lin
 * @Description:
 * @Date: Create：in 2020/12/29 22:13
 * @Modified By：
 */
public class BaseTemp {

    private int rowno;

    private String alaramId;

    private String appService;

    private String systemName;

    private String ipAddress;

    private List<CmdbTotal> tempList;

    public int getRowno() {
        return rowno;
    }

    public void setRowno(int rowno) {
        this.rowno = rowno;
    }

    public List<CmdbTotal> getTempList() {
        return tempList;
    }

    public void setTempList(List<CmdbTotal> tempList) {
        this.tempList = tempList;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAlaramId() {
        return alaramId;
    }

    public void setAlaramId(String alaramId) {
        this.alaramId = alaramId;
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
}
