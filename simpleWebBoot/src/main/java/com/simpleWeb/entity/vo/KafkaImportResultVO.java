package com.simpleWeb.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Kafka导入结果返回对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaImportResultVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Boolean success;
    
    private String message;
    
    private Integer totalCount;
    
    private Integer successCount;
    
    private Integer failCount;
    
    private String errorDetail;
}
