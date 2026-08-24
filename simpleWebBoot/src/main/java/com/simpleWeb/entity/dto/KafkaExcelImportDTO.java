package com.simpleWeb.entity.dto;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.io.Serializable;

/**
 * Excel导入数据传输对象
 */
@Data
public class KafkaExcelImportDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Alias("魔方名称")
    private String magicName;
    
    @Alias("主题名称")
    private String topicName;
    
    @Alias("用户权限")
    private String permissionType;
    
    @Alias("用户名称")
    private String userName;
    
    @Alias("设置密码")
    private String userPwd;
}
