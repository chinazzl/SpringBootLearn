package com.simpleWeb.controller;

import com.simpleWeb.entity.Constant;
import com.simpleWeb.entity.PO;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @Author: zhang zhao lin
 * @Description:
 * @Date:Create：in 2020/6/16 17:26
 * @Modified By：
 */
@RestController
@RequestMapping("/re")
public class MvcController {

    private static final String VTITLENAME_INIT = "地市名称,区县名称,供电单位,统计日期,";
    // #orgNo#appNo#statYm#actCode#consNo#consName#consSortCode#orderNo#stealTypeCode#errorid#topiccode
    private static final String SQLPARALIST_INIT_START = "orgNo#appNo#statYm#actCode#";
    private static final String SQLPARALIST_INIT_END = "errorid#topiccode#";


    @PostMapping("/v")
    public List<String> getParam(@RequestBody PO po) {
        String topicCode = po.getTopicCode();
        String tableName = po.getTableName();
        List<PO> dataList = po.getDataList();
        StringBuilder searchParam = new StringBuilder();
        // 1. List_info 表插入数据
        saveListInfo(dataList, topicCode, tableName);
        return null;
    }

    //    List_info 表插入数据
    private void saveListInfo(List<PO> dataList, String topicCode, String tableName) {
        String listInfoSequence = null;
        //存储 VTITLEMAP 字段
        StringBuilder vtitleNameMap = new StringBuilder();
        vtitleNameMap.append(VTITLENAME_INIT);
        // 存储 SQLPARALIST 字段参数拼接
        StringBuilder sqlParaList = new StringBuilder();
        sqlParaList.append(SQLPARALIST_INIT_START);

        List<PO> searchParamItem = new ArrayList<>();
        // 获取ListInfo所有的序列们
        List<String> listInfoSequences = new ArrayList<>();
        // 初始化需要拼接的列名
        for (PO po : dataList) {
            if (po.getColName_cn() != null && "1".equals(po.getIsShowFields())) {
                //拼接 vtitleNameMap
                vtitleNameMap.append(po.getColName_cn() + ",");
                //拼接 sqlParaList
                sqlParaList.append(po.getColName_en() + "#");

            }
            // 获取 搜索框的 项
            if (!StringUtils.isEmpty(po.getIsShowFields())&& !StringUtils.isEmpty(po.getEffectFlag())) {
                searchParamItem.add(po);
            }
        }
        sqlParaList.append(SQLPARALIST_INIT_END);
        System.out.println("展示列头中文名 ：" + vtitleNameMap.toString());
        System.out.println(" SQLPARALIST " + sqlParaList.toString());
        // 保存 搜索条件配置表
        saveSearchinfo(searchParamItem, listInfoSequence);

        // 保存 sql配置表
        saveSqlInfo(dataList, tableName, sqlParaList, listInfoSequences);
    }

    /**
     * 保存 sql 配置表
     *
     * @param dataList
     * @param tableName
     * @param sqlParaList
     * @param listInfoSequences
     */
    private void saveSqlInfo(List<PO> dataList, String tableName, StringBuilder sqlParaList, List<String> listInfoSequences) {
        //生成环节 9 —— 工单生成环节，未提交数
        saveLinkCode9(dataList, tableName, sqlParaList, listInfoSequences);
    }

    /**
     * 保存 环节9 sql Info 解析参数
     *
     * @param dataList          前端 传入 数据集合
     * @param tableName         表名
     * @param sqlParaList       sql查询参数
     * @param listInfoSequences listInfo的序列
     */
    private void saveLinkCode9(List<PO> dataList, String tableName, StringBuilder sqlParaList, List<String> listInfoSequences) {
        // 拼接 明细SQL语句
        StringBuilder detailSQL = new StringBuilder();
        // 存储展示的表中展示列名
        List<String> showColumnName = new ArrayList<>();
        // 存储 表格字段的 展示标准编码
        Map<String, String> columnStandardMap = new HashMap<>();
        // 存储 搜索条件的 标准编码
        Map<String, String> searchDropDown_standardMap = new HashMap<>();
        // 存储 搜索条件 精确查询
        Map<String, String> searchText_inputMap = new HashMap<>();
        // 存储 搜索条件 模糊查询
        Map<String, String> searchText_inputLikeMap = new HashMap<>();
        // 进行遍历判断
        for (PO po : dataList) {
            //  IsShowFields 表示 0 否 1 是 ， 下面判断除了 查询框 以外需要展示的明细列
            if ("1".equals(po.getIsShowFields()) && StringUtils.isEmpty(po.getCheckAndShowSort())) {
                showColumnName.add(po.getColName_en());
                //是否 表格字段需要进行转码
                if (!StringUtils.isEmpty(po.getIsStandCode())) {
                    columnStandardMap.put(po.getColName_en(), po.getIsStandCode());
                }
            }
            if (po.getIsShowFields() != null && po.getEffectFlag() != null) {
                // 下拉框查询
                if ("03".equals(po.getFiledCheckType())) {
                    searchDropDown_standardMap.put(po.getColName_en(), po.getIsStandCode());
                } else if ("02".equals(po.getFiledCheckType())) { // 模糊查询
                    searchText_inputLikeMap.put(po.getColName_en(), po.getIsStandCode());
                } else if ("01".equals(po.getFiledCheckType())) { // 精确查询
                    searchText_inputMap.put(po.getColName_en(), po.getIsStandCode());
                }
            }
        }

        /*
            生成SQL
         */
        String link9SQL = generateSQLLink9(dataList, tableName, listInfoSequences, columnStandardMap, searchDropDown_standardMap, searchText_inputLikeMap, searchText_inputMap, sqlParaList);

    }

    /**
     * 生成SQL
     *
     * @param dataList                   前端数据项
     * @param tableName                  表名
     * @param listInfoSequences          list Info 序列
     * @param columnStandardMap          展示列 存在下拉框 标准编码
     * @param searchDropDown_standardMap 搜索条件 存在标准编码下拉框展示
     * @param searchText_inputLikeMap    搜索条件 录入 模糊查询
     * @param searchText_inputMap        搜索条件 录入 精确查询
     * @param sqlParaList
     */
    private String generateSQLLink9(List<PO> dataList, String tableName, List<String> listInfoSequences, Map<String, String> columnStandardMap,
                                    Map<String, String> searchDropDown_standardMap,
                                    Map<String, String> searchText_inputLikeMap,
                                    Map<String, String> searchText_inputMap,
                                    StringBuilder sqlParaList) {
        // 拼接 明细SQL语句 总
        StringBuilder detailSQL = new StringBuilder();
        detailSQL.append(Constant.DETAILCOMMONSQL);
        // 表格中展示的 字段 初始化为空串
        StringBuilder showColumns = new StringBuilder("");
        // 表格非临时表的 查询字段
        StringBuilder showTotalColumns = new StringBuilder("");
        // 展示列 存在下拉框 sql片段
        String columnStandardSQLSegment = "";
        // 搜索框 存在下拉框 sql片段
        String searchDropDownSQLSegment = "";
        // 搜索框 模糊查询 sql 片段
        String searchTextLikeSQLSegment = "";
        // 搜索框 精确查询 sql片段
        String searchTextSQLSegment = "";

        //获取 传入的参数 SqlParaList 的相关索引
        String[] paraArray = sqlParaList.toString().split("#");
        int paraListLen = paraArray.length;

        for (PO po : dataList) {
            String columnName_en = po.getColName_en();

            //表格列 是否存在下拉选
            if (columnStandardMap.containsKey(columnName_en)) {
                columnStandardSQLSegment += Constant.DETAILSQLTransCode;
                columnStandardSQLSegment = columnStandardSQLSegment.replaceAll("#transCode", columnStandardMap.get(columnName_en))
                        .replaceAll("#transeTBAsias", "SP_" + columnName_en);

                showColumns.append("SP_" + columnName_en + ".").append("DIM_VALUE_NAME AS " + columnName_en);
            } else if (searchDropDown_standardMap.containsKey(columnName_en)) { // 搜索框展示 搜索条件 下拉框
                searchDropDownSQLSegment += Constant.DETAILSQLSEARCH_DROPDOWNEDITOR;
                searchDropDownSQLSegment = searchDropDownSQLSegment.replaceAll("#standardCode", searchDropDown_standardMap.get(columnName_en))
                        .replaceAll("#transeCodeIndexLast",String.valueOf(getArraysElementIndex(paraArray,columnName_en)))
                        .replaceAll("#transeCodeIndex",String.valueOf(getArraysElementIndex(paraArray,columnName_en) +1))
                        ;
            } else if (searchText_inputLikeMap.containsKey(columnName_en)) { // 搜索框展示 搜索条件 模糊查询
                searchTextLikeSQLSegment += Constant.DETAILSQLSEARCH_TEXTEDITORLIKE;
                searchTextLikeSQLSegment = searchTextLikeSQLSegment.replaceAll("#tableColumn", columnName_en)
                                            .replaceAll("#likeTextSearchLast",String.valueOf(getArraysElementIndex(paraArray,columnName_en)))
                                            .replaceAll("#likeTextSearch",String.valueOf(getArraysElementIndex(paraArray,columnName_en) +1));
            } else if (searchText_inputMap.containsKey(columnName_en)) { // 搜索框 搜索条件 精准查询
                searchTextSQLSegment += Constant.DETAILSQLSEARCH_TEXTEDITORFULL;
                searchTextSQLSegment = searchTextSQLSegment.replaceAll("#tableColumn", columnName_en)
                                        .replaceAll("#fullTextSearchLast",String.valueOf(getArraysElementIndex(paraArray,columnName_en)))
                                        .replaceAll("#fullTextSearch",String.valueOf(getArraysElementIndex(paraArray,columnName_en) +1));
            }
            //整理表格拼接lie
            if (!columnStandardMap.containsKey(columnName_en)) {
                showColumns.append("D.").append(columnName_en + ",");
            }
            showTotalColumns.append("DATA_DET.").append(columnName_en + ",");
        }
        String generateSQL = detailSQL.toString().replaceAll("#c1", showColumns.toString())
                .replaceAll("#columnTransSql", columnStandardSQLSegment)
                .replaceAll("#SEARCHPARAM", searchDropDownSQLSegment + searchTextLikeSQLSegment + searchTextSQLSegment)
                .replaceAll("#TABLENAME", tableName)
                .replaceAll("#RESULTSEARCH", showTotalColumns.toString())
                .replaceAll("#errorIndexLast", String.valueOf(paraListLen - 2))
                .replaceAll("#errorIndex", String.valueOf(paraListLen - 1))
                .replaceAll("#topicCodeIndexLast",String.valueOf(paraListLen - 1))
                .replaceAll("#topicCodeIndex",String.valueOf(paraListLen));
        System.out.println(generateSQL);
        return generateSQL;
    }

    /**
     * 获取 数组中 指定元素的索引
     *
     * @param os
     * @param o
     * @return
     */
    private int getArraysElementIndex(Object[] os, Object o) {
        if (os != null) {
            return Arrays.asList(os).indexOf(o);
        }
        return 0;

    }

    // 保存 searchConfig
    private void saveSearchinfo(List<PO> searchParamItem, String listInfoSequence) {

        // 前台 表格数据 排序规则有可能不是按照顺序执行，则进行二次排序
        Collections.sort(searchParamItem, new Comparator<PO>() {
            @Override
            public int compare(PO o1, PO o2) {
                return Integer.parseInt(o2.getCheckAndShowSort()) > Integer.parseInt(o1.getCheckAndShowSort()) ? 1 : -1;
            }
        });
        System.out.println("经历排序后的集合：" + searchParamItem.toString());

        //进行循环插入searchParam
        for (PO po : searchParamItem) {
            // do something....
        }

    }
}
