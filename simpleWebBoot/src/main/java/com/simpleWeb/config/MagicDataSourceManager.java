package com.simpleWeb.config;

import com.simpleWeb.mapper.DynamicMapper;
import com.simpleWeb.properties.DataSourceProperties;
import com.zaxxer.hikari.HikariDataSource;
import groovy.util.logging.Slf4j;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author: zhaolin
 * @Date: 2025/8/16
 * @Description:
 **/
@lombok.extern.slf4j.Slf4j
@Component
@Slf4j
public class MagicDataSourceManager {
    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();
    private final Map<String, SqlSessionFactory> sqlSessionFactoryMapMap = new ConcurrentHashMap<>();

    @Resource
    private DataSourceProperties dataSourceProperties;

    @PostConstruct
    public void init() {
        for (DataSourceProperties.DataSourceConfig config : dataSourceProperties.getConfigs()) {
            createDataSource(config);
        }
    }

    private void createDataSource(DataSourceProperties.DataSourceConfig config) {
        try {
            // 创建数据源
            HikariDataSource hikariDataSource = new HikariDataSource();
            hikariDataSource.setDriverClassName(config.getDriverClassName());
            hikariDataSource.setJdbcUrl(config.getUrl());
            hikariDataSource.setUsername(config.getUsername());
            hikariDataSource.setPassword(config.getPassword());
            // 优化连接池配置
            hikariDataSource.setMaximumPoolSize(Math.max(10, Runtime.getRuntime().availableProcessors() * 2));
            hikariDataSource.setMinimumIdle(2);
            hikariDataSource.setConnectionTimeout(30000);
            hikariDataSource.setIdleTimeout(600000);
            hikariDataSource.setMaxLifetime(1800000);
            hikariDataSource.setLeakDetectionThreshold(60000);
            // 创建sqlSession
            SqlSessionFactory sqlSessionFactory = createSqlSessionFactory(hikariDataSource);
            dataSourceMap.put(config.getName(), hikariDataSource);
            sqlSessionFactoryMapMap.put(config.getName(), sqlSessionFactory);
            log.info("初始化数据源成功: {}, 连接URL: {}", config.getName(),
                    maskPassword(config.getUrl()));
        }catch (Exception e){
            log.error("初始化数据源失败，异常：{}",e.getMessage(),e);
        }
    }

    private SqlSessionFactory createSqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCallSettersOnNulls(true);
        configuration.setUseGeneratedKeys(true);
        // 手动添加Mapper接口到Configuration
        try {
            configuration.addMapper(DynamicMapper.class);
            log.debug("成功注册 DynamicSqlMapper 接口");
        } catch (Exception e) {
            log.error("注册 DynamicSqlMapper 接口失败", e);
            throw e;
        }
        sqlSessionFactoryBean.setConfiguration(configuration);

        // 设置 Mapper XML 文件位置
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            org.springframework.core.io.Resource[] mapperResources = resolver.getResources("classpath*:mappings/dynamic/*.xml");
            if (mapperResources.length > 0) {
                sqlSessionFactoryBean.setMapperLocations(mapperResources);
                log.debug("加载了 {} 个 Mapper XML 文件", mapperResources.length);
            } else {
                log.warn("未找到 Mapper XML 文件，将使用注解方式");
            }
        } catch (Exception e) {
            log.warn("加载 Mapper XML 文件失败，将使用注解方式: {}", e.getMessage());
        }

        return sqlSessionFactoryBean.getObject();
    }

    public SqlSession getSqlSession(String datasourceName) throws Exception {
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryMapMap.get(datasourceName);
        if (sqlSessionFactory == null) {
            throw new IllegalArgumentException("数据源不存在: " + datasourceName);
        }
        return sqlSessionFactory.openSession(true);
    }

    public <T> T withSqlSession(String datasourceName, Function<SqlSession,T> callable) throws Exception {
        try (SqlSession sqlSession = getSqlSession(datasourceName)) {
            return callable.apply(sqlSession);
        }catch (Exception e){
            log.error("执行数据库操作失败，数据源：{}",datasourceName,e);
            throw new RuntimeException("数据库操作失败",e);
        }
    }

    /**
     * 获取数据源信息
     */
    public Map<String, Object> getDataSourceInfo(String dataSourceName) {
        DataSource dataSource = dataSourceMap.get(dataSourceName);
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDS = (HikariDataSource) dataSource;
            Map<String, Object> info = new HashMap<>();
            info.put("name", dataSourceName);
            info.put("jdbcUrl", maskPassword(hikariDS.getJdbcUrl()));
            info.put("maximumPoolSize", hikariDS.getMaximumPoolSize());
            info.put("activeConnections", hikariDS.getHikariPoolMXBean().getActiveConnections());
            info.put("idleConnections", hikariDS.getHikariPoolMXBean().getIdleConnections());
            info.put("totalConnections", hikariDS.getHikariPoolMXBean().getTotalConnections());
            info.put("isHealthy", testConnection(dataSourceName));
            return info;
        }
        return Collections.emptyMap();
    }
    public Set<String> getDataSourceNames() {
        return dataSourceMap.keySet();
    }

    /**
     * 检查数据源连接状态
     */
    public boolean testConnection(String dataSourceName) {
        try {
            return withSqlSession(dataSourceName, sqlSession -> {
                sqlSession.selectOne("SELECT 1");
                return true;
            });
        } catch (Exception e) {
            log.warn("数据源连接测试失败: {}", dataSourceName, e);
            return false;
        }
    }

    private String maskPassword(String url) {
        if (url == null) return null;
        return url.replaceAll("password=[^;&]*", "password=***");
    }

    @PreDestroy
    public void destroy() {
        log.info("开始关闭数据源连接池...");
        dataSourceMap.forEach((name, dataSource) -> {
            try {
                if (dataSource instanceof HikariDataSource) {
                    ((HikariDataSource) dataSource).close();
                    log.info("数据源 {} 已关闭", name);
                }
            } catch (Exception e) {
                log.error("关闭数据源失败: {}", name, e);
            }
        });
        log.info("所有数据源已关闭");
    }

}
