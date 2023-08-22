package org.template.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月15日 14:21
 * @Description:
 *   id          VARCHAR2(255),
 *   target      VARCHAR2(255),
 *   target_type VARCHAR2(255),
 *   targetid    VARCHAR2(255),
 *   target_key  VARCHAR2(255),
 *   value_text  VARCHAR2(255),
 *   value_type  VARCHAR2(255),
 *   instanceid  VARCHAR2(255)
 **********************************/
@Data
@TableName(value = "T_CMDB_INFO")
public class TCmdbInfo {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String target;
    @TableField(value = "TARGET_TYPE",jdbcType = JdbcType.VARCHAR)
    private String targetType;
    private String targetId;
    @TableField(value = "TAG_KEY",jdbcType = JdbcType.VARCHAR)
    private String targetKey;
    @TableField(value = "VALUE_TEXT",jdbcType = JdbcType.VARCHAR)
    private String valueText;
    @TableField(value = "VALUE_TYPE",jdbcType = JdbcType.VARCHAR)
    private String valueType;

    private String instanceId;
}
