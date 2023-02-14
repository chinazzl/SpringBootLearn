package org.basic.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/4 17:07
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Result<T> {
    
    @Builder.Default
    private int status = HttpStatus.OK.value();
    
    private T data;
    @Builder.Default
    private String message = "成功";
}
