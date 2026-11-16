package org.template.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 启动自检（fail-fast），在 DataSource 建池前先把三类必死问题拦下：
 * <ol>
 *   <li>生产（sm4.enabled=true）缺密钥材料 / 密钥未加载；</li>
 *   <li>生产出现 username/password 明文；</li>
 *   <li>密钥文件不存在 / 为空 / 权限非 600（实际校验在 Sm4KeyManager，异常会冒泡到此）。</li>
 * </ol>
 * 全部检查只打印状态与脱敏值，绝不打印密钥与口令明文。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class Sm4StartupValidator implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(Sm4StartupValidator.class);

    private final Sm4Properties properties;
    private final Sm4KeyManager keyManager;
    private final Sm4CipherService cipherService;
    private final Environment env;

    public Sm4StartupValidator(Sm4Properties properties,
                               Sm4KeyManager keyManager,
                               Sm4CipherService cipherService,
                               Environment env) {
        this.properties = properties;
        this.keyManager = keyManager;
        this.cipherService = cipherService;
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) {
        String usernameRaw = env.getProperty("spring.datasource.username");
        String passwordRaw = env.getProperty("spring.datasource.password");

        if (usernameRaw == null || usernameRaw.isEmpty()
                || passwordRaw == null || passwordRaw.isEmpty()) {
            throw new SmCryptoException("spring.datasource.username/password 未配置，拒绝启动");
        }

        boolean userEnc = cipherService.isEncrypted(usernameRaw);
        boolean passEnc = cipherService.isEncrypted(passwordRaw);

        if (!properties.isEnabled()) {
            // 本地开发模式：允许明文，但密文+未启用属于配置矛盾，直接拒绝
            if (userEnc || passEnc) {
                throw new SmCryptoException("检测到 SM4 密文但 sm4.enabled=false，拒绝启动（请启用 SM4 或改回明文仅本地调试）");
            }
            log.warn("SM4 未启用（sm4.enabled=false）：username/password 为明文，仅允许本地开发，生产禁止");
            return;
        }

        // 生产模式：密钥必须已加载
        if (!keyManager.isLoaded()) {
            throw new SmCryptoException("SM4 已启用但密钥未加载（请检查 SM4_KEY 环境变量或 sm4.key-file），拒绝启动");
        }
        // 生产模式：用户名/口令必须全部密文化
        if (!userEnc || !passEnc) {
            throw new SmCryptoException(
                    "生产环境 username/password 必须为 SM4(...) 密文（当前 username加密=" + userEnc
                            + " password加密=" + passEnc + "），请用 Sm4CryptoTool 加密后回填，拒绝启动");
        }
        if (properties.isLogKeyLoaded()) {
            log.info("SM4 启动自检通过：密钥已加载（内容不打印），username={} 已密文化",
                    SensitiveMaskUtils.maskUsername(usernameRaw));
        } else {
            log.info("SM4 启动自检通过");
        }
    }
}
