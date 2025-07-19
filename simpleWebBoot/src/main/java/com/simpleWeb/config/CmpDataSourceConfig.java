package com.simpleWeb.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author: zhaolin
 * @Date: 2025/7/17
 * @Description:
 **/
@Configuration
@MapperScan(basePackages = "com.simpleWeb.mapper.cmp",sqlSessionTemplateRef = "cmpSqlSessionTemplate")
public class CmpDataSourceConfig {

    @Bean("cmpDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.cmp")
    public DataSource cmpDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "cmpSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("cmpDataSource") DataSource cmpDataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(cmpDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mappings/cmp/*.xml"));
        sessionFactory.setTypeAliasesPackage("com.simpleWeb.entity.db.cmp");
        // 设置MyBatis配置
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(true);
        configuration.setLazyLoadingEnabled(true);
        sessionFactory.setConfiguration(configuration);

        return sessionFactory.getObject();
    }

    @Bean(name = "cmpTransactionManager")
    public DataSourceTransactionManager cmpTransactionManager(@Qualifier("cmpDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "cmpSqlSessionTemplate")
    public SqlSessionTemplate cmpSqlSessionTemplate(@Qualifier("cmpSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
