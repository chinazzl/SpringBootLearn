package com.simpleWeb.entity;

import java.util.List;

/**
 * @Author: zhang zhao lin
 * @Description:
 * @Date:Create：in 2020/6/16 17:27
 * @Modified By：
 */
public class PO {

    private String tableName;

    private String topicCode;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTopicCode() {
        return topicCode;
    }

    public void setTopicCode(String topicCode) {
        this.topicCode = topicCode;
    }

    private String colName_cn;

    private String colName_en;

    private String colType;

    private String isShowFields;

    private String filedCheckType;

    private String isStandCode;

    private String effectFlag;

    private String checkAndShowSort;

    private List<PO> dataList;

    public List<PO> getDataList() {
        return dataList;
    }

    public void setDataList(List<PO> dataList) {
        this.dataList = dataList;
    }

    public String getColName_cn() {
        return colName_cn;
    }

    public void setColName_cn(String colName_cn) {
        this.colName_cn = colName_cn;
    }

    public String getColName_en() {
        return colName_en;
    }

    public void setColName_en(String colName_en) {
        this.colName_en = colName_en;
    }

    public String getColType() {
        return colType;
    }

    public void setColType(String colType) {
        this.colType = colType;
    }

    public String getIsShowFields() {
        return isShowFields;
    }

    public void setIsShowFields(String isShowFields) {
        this.isShowFields = isShowFields;
    }

    public String getFiledCheckType() {
        return filedCheckType;
    }

    public void setFiledCheckType(String filedCheckType) {
        this.filedCheckType = filedCheckType;
    }

    public String getIsStandCode() {
        return isStandCode;
    }

    public void setIsStandCode(String isStandCode) {
        this.isStandCode = isStandCode;
    }

    public String getEffectFlag() {
        return effectFlag;
    }

    public void setEffectFlag(String effectFlag) {
        this.effectFlag = effectFlag;
    }

    public String getCheckAndShowSort() {
        return checkAndShowSort;
    }

    public void setCheckAndShowSort(String checkAndShowSort) {
        this.checkAndShowSort = checkAndShowSort;
    }

    @Override
    public String toString() {
        return "PO{" +
                "tableName='" + tableName + '\'' +
                ", topicCode='" + topicCode + '\'' +
                ", colName_cn='" + colName_cn + '\'' +
                ", colName_en='" + colName_en + '\'' +
                ", colType='" + colType + '\'' +
                ", isShowFields='" + isShowFields + '\'' +
                ", filedCheckType='" + filedCheckType + '\'' +
                ", isStandCode='" + isStandCode + '\'' +
                ", effectFlag='" + effectFlag + '\'' +
                ", checkAndShowSort='" + checkAndShowSort + '\'' +
                ", dataList=" + dataList +
                '}';
    }
}
