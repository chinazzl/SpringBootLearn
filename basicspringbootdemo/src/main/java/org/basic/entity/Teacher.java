package org.basic.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.basic.config.jackson.DateJsonSerialize;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/5 15:58
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class Teacher extends RequestBase {

    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotNull(message = "年龄不能为空")
    @Range(min = 0, max = 150, message = "年龄不合法")
    private Integer age;
    @NotNull(message = "生日不能为空")
    @JsonSerialize(using = DateJsonSerialize.class)
    private Date birthday;

}
