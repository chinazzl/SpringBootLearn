package org.example;

import lombok.Data;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月11日 17:51
 * @Description:
 **********************************/
@Data
public class User {

    private String name;

    private String password;

    private String email;

    private String phone;

    private Boolean isBoy;
}
