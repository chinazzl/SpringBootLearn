package org.basic.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/4 18:10
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class RequestData {

    // 加密的文本
    private String text;
}
