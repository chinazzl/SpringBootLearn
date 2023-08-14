package com.simpleWeb.entity;

import java.util.List;

/**
 * @Author: zhang zhao lin
 * @Description:
 * @Date:Create：in 2020/12/29 22:21
 * @Modified By：
 */
public class FinalPO {
    private  BaseTemp baseTemp;
    //保存 分组的 数据
    private List<CmdbTotal> tempList;

    public BaseTemp getBaseTemp() {
        return baseTemp;
    }

    public void setBaseTemp(BaseTemp baseTemp) {
        this.baseTemp = baseTemp;
    }

    public List<CmdbTotal> getTempList() {
        return tempList;
    }

    public void setTempList(List<CmdbTotal> tempList) {
        this.tempList = tempList;
    }
}
