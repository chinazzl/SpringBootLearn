package org.template.crypto;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 国密 SM4 单密钥配置（无密钥分片）。
 *
 * <p>密钥本身绝不写在 yml / 代码 / Git 中，只通过以下其一提供：
 * <ul>
 *   <li>环境变量：{@code keyEnv} 指定的变量名（推荐 K8s Secret / 运维保险柜注入），变量值为密钥材料
 *       （UTF-8 字符串或 Base64，任意长度均可，内部经 SM3 摘要派生 16 字节 SM4 密钥）；</li>
 *   <li>密钥文件：{@code keyFile} 路径，文件内容即密钥材料，文件权限必须 600（仅属主可读写），
 *       否则启动拒绝（见 {@link Sm4KeyManager}）。</li>
 * </ul>
 * 优先级：keyFile（存在且非空） &gt; keyEnv 环境变量。
 */
@Component
@ConfigurationProperties(prefix = "sm4")
public class Sm4Properties {

    /** 总开关；false 仅允许本地开发明文直连，生产必须 true */
    private boolean enabled = true;

    /** 密钥材料所在环境变量名，默认 SM4_KEY */
    private String keyEnv = "SM4_KEY";

    /** 密钥文件路径，为空表示不使用文件；如 /run/secrets/sm4.key */
    private String keyFile = "";

    /** 是否强制校验密钥文件最小权限（600），不满足则拒绝启动 */
    private boolean strictFilePermission = true;

    /** 是否打印“密钥已加载”（只打印状态，绝不打印密钥内容） */
    private boolean logKeyLoaded = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getKeyEnv() {
        return keyEnv;
    }

    public void setKeyEnv(String keyEnv) {
        this.keyEnv = keyEnv;
    }

    public String getKeyFile() {
        return keyFile;
    }

    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }

    public boolean isStrictFilePermission() {
        return strictFilePermission;
    }

    public void setStrictFilePermission(boolean strictFilePermission) {
        this.strictFilePermission = strictFilePermission;
    }

    public boolean isLogKeyLoaded() {
        return logKeyLoaded;
    }

    public void setLogKeyLoaded(boolean logKeyLoaded) {
        this.logKeyLoaded = logKeyLoaded;
    }
}
