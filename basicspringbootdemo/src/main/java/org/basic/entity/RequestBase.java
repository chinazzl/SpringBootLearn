package org.basic.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/4 18:11
 * @Description:
 */
@Data
@EqualsAndHashCode
public class RequestBase {

    private Long currentTimeMillis;
}
