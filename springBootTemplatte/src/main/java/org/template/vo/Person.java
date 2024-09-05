package org.template.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.template.config.annotation.HeaderName;

import java.io.Serializable;
import java.math.BigDecimal;

/**********************************
 * @author zhang zhao lin
 * @date 2024年09月05日 14:37
 * @Description:
 **********************************/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person implements Serializable {

    private static final long serialVersionUID = -3621354299691315539L;

    //班级名称
    private String className;
    //班级分数
    private double classScore;
    //小组名称
    private String groupName;
    //小组分数
    private double groupScore;
    //人物姓名
    private String personName;
    //人物总分
    private double personScore;
    //学科名称
    private String subjectName;
    //学科分数
    private double subjectScore;
}
