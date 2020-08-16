package com.juxingtech.helmet.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.juxingtech.helmet.common.enums.DataSourceTypeEnum;
import com.juxingtech.helmet.framework.datasource.DynamicDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class DruidConfig {

    @Autowired
    private PaginationInterceptor paginationInterceptor;

    @Bean(name = "mysql")
    @ConfigurationProperties(prefix = "spring.datasource.druid.mysql")
    public DataSource mysqlDataSource(){
        return  DruidDataSourceBuilder.create().build();
    }

    // @Bean(name = "oracle")
    @ConfigurationProperties(prefix = "spring.datasource.druid.oracle")
    public DataSource oracleDataSource(){
        return  DruidDataSourceBuilder.create().build();
    }


    @Bean
    @Primary
    public DataSource dynamicDataSource(
            @Qualifier("mysql") DataSource mysqlDataSource
            // , @Qualifier("oracle") DataSource oracleDataSource
    ){

        Map<Object,Object> targetDataSources=new HashMap<>();
        targetDataSources.put(DataSourceTypeEnum.mysql.getValue(),mysqlDataSource);
        //targetDataSources.put(DataSourceTypeEnum.oracle.getValue(),oracleDataSource);

        DynamicDataSource dynamicDataSource=new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(mysqlDataSource);
        return dynamicDataSource;
    }


    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory=new MybatisSqlSessionFactoryBean();
        // sqlSessionFactory.setDataSource(dynamicDataSource(mysqlDataSource(),oracleDataSource()));
        sqlSessionFactory.setDataSource(mysqlDataSource());
        MybatisConfiguration configuration=new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);
        sqlSessionFactory.setConfiguration(configuration);
        sqlSessionFactory.setPlugins(new Interceptor[]{
                paginationInterceptor
        });
        return sqlSessionFactory.getObject();
    }
}
