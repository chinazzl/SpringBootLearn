package com.simpleWeb.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author: zhaolin
 * @Date: 2025/1/22
 * @Description:
 **/
@Data
@Component
public class TrapSource {

    private String name;        // 源名称，如 "金融信贷集群"
    private String ip;          // Trap源IP
    private int port;           // 端口
    private String username;    // 用户名
    private String password;    // 密码
    private String authPassword; // 加密密码
}
