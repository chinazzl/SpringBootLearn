package org.template.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.template.crypto.SensitiveMaskUtils;
import org.template.crypto.Sm4CipherService;
import org.template.crypto.Sm4Properties;
import org.template.crypto.SmCryptoException;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * 数据源显式配置（单数据源，HikariCP + MyBatis / MyBatis-Plus）。
 *
 * <p>职责：
 * <ul>
 *   <li>从 {@code application.yml} 读取 {@code spring.datasource.url/username/password}，
 *       其中 {@code username/password} 支持 {@code SM4(Base64)} 密文，启动时经国密 SM4 解密；</li>
 *   <li>解密中间变量使用 {@code char[]}，设置进 Hikari 后立即擦除，满足监管第 3 条；</li>
 *   <li>日志只打印脱敏后的用户名 / URL，绝不打印口令明文，满足监管第 2 条；</li>
 *   <li>生产环境（{@code sm4.enabled=true}）下口令/用户名为明文则拒绝启动，
 *       倒逼配置中心只存密文，满足监管第 1 条。</li>
 * </ul>
 * MyBatis / MyBatis-Plus 无需额外配置：本类暴露 {@link DataSource} 后，
 * {@code mybatis-plus-boot-starter} 自动装配 {@code SqlSessionFactory}，
 * {@code @MapperScan} 见启动类。
 */
@Configuration
@EnableTransactionManagement
public class SecureDataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(SecureDataSourceConfig.class);

    private final Environment env;
    private final Sm4Properties sm4Properties;
    private final Sm4CipherService cipherService;

    public SecureDataSourceConfig(Environment env,
                                  Sm4Properties sm4Properties,
                                  Sm4CipherService cipherService) {
        this.env = env;
        this.sm4Properties = sm4Properties;
        this.cipherService = cipherService;
    }

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        String url = env.getProperty("spring.datasource.url");
        String driver = env.getProperty("spring.datasource.driver-class-name");
        String usernameRaw = env.getProperty("spring.datasource.username");
        String passwordRaw = env.getProperty("spring.datasource.password");

        if (url == null || url.isEmpty()) {
            throw new SmCryptoException("spring.datasource.url 未配置，拒绝启动");
        }

        char[] usernameChars = resolveSecret("spring.datasource.username", usernameRaw, true);
        char[] passwordChars = resolveSecret("spring.datasource.password", passwordRaw, true);
        String username = null;
        String password = null;
        try {
            username = usernameChars == null ? null : new String(usernameChars);
            password = passwordChars == null ? null : new String(passwordChars);

            HikariConfig config = new HikariConfig();
            if (driver != null && !driver.isEmpty()) {
                config.setDriverClassName(driver);
            }
            config.setJdbcUrl(url);
            if (username != null) {
                config.setUsername(username);
            }
            if (password != null) {
                config.setPassword(password);
            }
            // Hikari 池参数（与 application.yml 保持一致，有则覆盖）
            bindHikari(config);

            HikariDataSource ds = new HikariDataSource(config);
            log.info("数据源初始化完成 url={} username={}",
                    SensitiveMaskUtils.maskJdbcUrl(url),
                    SensitiveMaskUtils.maskUsername(username));
            log.info("SM4 密钥状态：{}", sm4Properties.isEnabled() ? "已加载（内容不打印）" : "未启用");
            return ds;
        } finally {
            // 监管第 3 条：初始化后立即将解密变量置空
            if (usernameChars != null) {
                Arrays.fill(usernameChars, '\0');
            }
            if (passwordChars != null) {
                Arrays.fill(passwordChars, '\0');
            }
            // username/password 的 String 形态交由 Hikari 持有后，局部引用尽快断开
            username = null;
            password = null;
        }
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 解析用户名/口令：SM4(...) 则解密为 char[]；明文则按 sm4.enabled 决定放行或拒绝。
     */
    private char[] resolveSecret(String key, String raw, boolean required) {
        if (raw == null || raw.isEmpty()) {
            if (required) {
                throw new SmCryptoException(key + " 未配置，拒绝启动");
            }
            return null;
        }
        if (cipherService.isEncrypted(raw)) {
            if (!sm4Properties.isEnabled()) {
                throw new SmCryptoException("检测到 SM4 密文但 sm4.enabled=false，拒绝启动");
            }
            return cipherService.decryptToChars(raw);
        }
        // 明文分支：生产强制加密，本地开发允许明文但告警
        if (sm4Properties.isEnabled()) {
            throw new SmCryptoException(
                    key + " 为明文，生产环境禁止明文（请用 Sm4CryptoTool 加密后填 SM4(...)），拒绝启动");
        }
        log.warn("{} 使用明文（仅允许本地开发，生产禁止）", key);
        return raw.toCharArray();
    }

    private void bindHikari(HikariConfig config) {
        setIfPresent("spring.datasource.hikari.pool-name", v -> config.setPoolName(v));
        setLongIfPresent("spring.datasource.hikari.connection-timeout", config::setConnectionTimeout);
        setLongIfPresent("spring.datasource.hikari.idle-timeout", config::setIdleTimeout);
        setLongIfPresent("spring.datasource.hikari.max-lifetime", config::setMaxLifetime);
        setIntIfPresent("spring.datasource.hikari.minimum-idle", config::setMinimumIdle);
        setIntIfPresent("spring.datasource.hikari.maximum-pool-size", config::setMaximumPoolSize);
        setBoolIfPresent("spring.datasource.hikari.auto-commit", config::setAutoCommit);
        setIfPresent("spring.datasource.hikari.connection-test-query", v -> config.setConnectionTestQuery(v));
    }

    private void setIfPresent(String key, java.util.function.Consumer<String> setter) {
        String v = env.getProperty(key);
        if (v != null && !v.isEmpty()) {
            setter.accept(v);
        }
    }

    private void setLongIfPresent(String key, java.util.function.Consumer<Long> setter) {
        String v = env.getProperty(key);
        if (v != null && !v.isEmpty()) {
            setter.accept(Long.parseLong(v.trim()));
        }
    }

    private void setIntIfPresent(String key, java.util.function.Consumer<Integer> setter) {
        String v = env.getProperty(key);
        if (v != null && !v.isEmpty()) {
            setter.accept(Integer.parseInt(v.trim()));
        }
    }

    private void setBoolIfPresent(String key, java.util.function.Consumer<Boolean> setter) {
        String v = env.getProperty(key);
        if (v != null && !v.isEmpty()) {
            setter.accept(Boolean.parseBoolean(v.trim()));
        }
    }
}
