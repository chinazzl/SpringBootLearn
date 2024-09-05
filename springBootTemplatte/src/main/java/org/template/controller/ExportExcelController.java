package org.template.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.template.vo.Person;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.template.json.PersonData.*;

/**********************************
 * @author zhang zhao lin
 * @date 2024年09月05日 15:14
 * @Description:
 **********************************/
@RestController
@RequestMapping("/export")
public class ExportExcelController {


    @GetMapping("/person")
    public void export(HttpServletRequest request, HttpServletResponse response) {
        List<String> rowHead = CollUtil.newArrayList("班级名称","班级分数","小组名称","小组分数",
                "角色姓名","角色总分","学科名称","学科分数");
        //3.通过ExcelUtil.getBigWriter()创建Writer对象，BigExcelWriter用于大数据量的导出，不会引起溢出；
        ExcelWriter writer = ExcelUtil.getBigWriter();
        //4.写入标题
        writer.writeHeadRow(rowHead);
        ServletOutputStream out = null;
        try {
            //6.定义容器保存人物数据
            List<List<Object>> rows = new LinkedList<>();
            //7.按照班级进行分组
            LinkedHashMap<String, List<Person>> classList = persons.stream().collect(Collectors.groupingBy(item -> item.getClassName(),
                    LinkedHashMap::new, Collectors.toList()));
            //8.定义起始行（方便分组后合并时从哪一行开始）
            //因为标题已经占了一行，所以数据从第二行开始写（excel第一行索引为0）
            //因需要合并到人物分数单元格所以需定义如下起始坐标
            int indexClassName = 1;   //班级名称起始行
            int indexClassScore = 1;
            int indexGroupName = 1;
            int indexGroupScore = 1;
            int indexPersonName = 1;
            int indexPersonScore = 1;
            //9.遍历按班级名分组后的list（用entrySet效率比keySet效率高）
            for (Map.Entry<String, List<Person>> classNameListEntry : classList.entrySet()) {
                //10.获取按照班级名分组后的集合
                List<Person> classValue = classNameListEntry.getValue();
                //11.计算此集合的长度
                int classSize = classValue.size();
                //12.如果只有一行数据不能调用merge方法合并数据，否则会报错
                if (classSize == 1) {
                    indexClassName += classSize;
                    indexClassScore += classSize;
                    indexGroupName += classSize;
                    indexGroupScore += classSize;
                    indexPersonName += classSize;
                    indexPersonScore += classSize;
                }else {
                    //13.根据班级名称进行合并单元格
                    //合并行，第一个参数是合并行的开始行号（行号从0开始），第二个参数是合并行的结束行号，第三个参数是合并的列号开始(列号从0开始)，
                    //第四个参数是合并的列号结束，第五个参数是合并后的内容，null不设置，第六个参数指是否支持设置样式，true指的是。
                    writer.merge(indexClassName, indexClassName + classSize - 1, 0, 0, null, true);
                    //14.合并完后起始索引移到下一个合并点
                    indexClassName += classSize;
                    //15.因为班级分数与班级名称相关联，所以不需要对班级分数分组，直接在此基础上对班级分数合并
                    writer.merge(indexClassScore, indexClassScore + classSize - 1, 1, 1, null, true);
                    indexClassScore += classSize;
                    //16.按照小组名进行分组（以下分组与班级名合并类似）
                    LinkedHashMap<String, List<Person>> groupList = classValue.stream().collect(Collectors.groupingBy(Person::getGroupName,LinkedHashMap::new, Collectors.toList()));
                    for (Map.Entry<String, List<Person>> groupNameEntry : groupList.entrySet()) {
                        List<Person> groupValue = groupNameEntry.getValue();
                        int groupSize = groupValue.size();
                        if (groupSize == 1) {
                            indexGroupName += groupSize;
                            indexGroupScore += groupSize;
                            indexPersonName += classSize;
                            indexPersonScore += classSize;
                        } else {
                            writer.merge(indexGroupName,indexGroupName + groupSize -1,2,2,null,true);
                            indexGroupName += groupSize;
                            writer.merge(indexGroupScore,indexGroupScore + groupSize -1, 3,3,null,true);
                            indexGroupScore += groupSize;
                            LinkedHashMap<String, List<Person>> personList = groupValue.stream().collect(Collectors.groupingBy(Person::getPersonName, LinkedHashMap::new, Collectors.toList()));
                            for (Map.Entry<String, List<Person>> personEntry : personList.entrySet()) {
                                List<Person> personGroupList = personEntry.getValue();
                                int personSize = personGroupList.size();
                                if (personSize == 1) {
                                    indexPersonName += personSize;
                                    indexPersonScore += personSize;
                                } else {
                                    writer.merge(indexPersonName,indexPersonName + personSize -1,4,4,null,true);
                                    indexPersonName += personSize;
                                    writer.merge(indexPersonScore,indexPersonScore + personSize -1,5,5,null,true);
                                    indexPersonScore += personSize;
                                }

                            }
                        }
                    }
                }
                classValue.forEach(cList -> {
                    List<Object> rowA = null;
                    rowA = CollUtil.newArrayList(
                            cList.getClassName(),
                            cList.getClassScore(),
                            cList.getGroupName(),
                            cList.getGroupScore(),
                            cList.getPersonName(),
                            cList.getPersonScore(),
                            cList.getSubjectName(),
                            cList.getSubjectScore()
                    );
                    rows.add(rowA);
                });
                // 一次性写出内容，使用默认样式，强制输出标题
                writer.write(rows, true);
                //response为HttpServletResponse对象
                response.setContentType("application/vnd.ms-excel;charset=utf-8");
                //获取当前日期作为文件名
                //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
                //response.setHeader("Content-Disposition", "attachment;filename=file.xlsx");
                response.setHeader("Content-Disposition", "attachment;filename=report_"+ DateUtil.today() +".xlsx");
                out = response.getOutputStream();
                writer.flush(out, true);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关闭输出Servlet流
            IoUtil.close(out);
            //关闭writer，释放内存
            writer.close();
        }
    }
}
