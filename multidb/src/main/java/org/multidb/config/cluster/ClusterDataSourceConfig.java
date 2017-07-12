package org.multidb.config.cluster;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Created by Felix on 2017/7/12.
 */
@Configuration
@MapperScan(basePackages =ClusterDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "clusterSqlSessionFactory")
public class ClusterDataSourceConfig {
    static final String PACKAGE = "org.multidb.dao.cluster";
    static final String MAPPER_LOCATION = "classpath:mapper/cluster/*.xml";

    @Value("${cluster.datasource.url}")
    private String url;

    @Value("${cluster.datasource.username}")
    private String user;

    @Value("${cluster.datasource.password}")
    private String password;

    @Value("${cluster.datasource.driverClassName}")
    private String driverClass;

    @Bean(name = "clusterDataSource")
    public DataSource clusterDataSource(){
        DruidDataSource clusterDruidDataSource = new DruidDataSource();
        clusterDruidDataSource.setUrl(url);
        clusterDruidDataSource.setUsername(user);
        clusterDruidDataSource.setPassword(password);
        clusterDruidDataSource.setDriverClassName(driverClass);
        return clusterDruidDataSource;
    }
    @Bean(name = "clusterTransactionManager")
    public DataSourceTransactionManager clusterTransactionManager(){
        return new DataSourceTransactionManager(clusterDataSource());
    }
    @Bean(name = "clusterSqlSessionFactory")
    public SqlSessionFactory clusterSqlSession(@Qualifier("clusterDataSource") DataSource clusterDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(clusterDataSource);
        sqlSessionFactoryBean.setMapperLocations( new PathMatchingResourcePatternResolver()
                .getResources(ClusterDataSourceConfig.MAPPER_LOCATION));
        return sqlSessionFactoryBean.getObject();
    }
}
