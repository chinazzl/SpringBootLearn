package org.template.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @author: zhang zhao lin
 * @Date: Create in 2023/8/14 下午10:18
 * @Modified By：
 * @Description:
 */
@Data
@Builder
public class User {

    private Integer id;
    private String userName;
    private String passWord;
}
