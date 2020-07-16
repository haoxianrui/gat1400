package com.juxingtech.helmet.framework.aspect;

import com.juxingtech.helmet.common.enums.DataSourceTypeEnum;
import com.juxingtech.helmet.framework.datasource.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class DataSourceAspect {
    @Pointcut("execution(* com.juxingtech.helmet.mapper.mysql..*.*(..))")
    private void mysqlAspect(){

    }

    @Pointcut("execution(* com.juxingtech.helmet.mapper.oracle..*.*(..))")
    private void oracleAspect(){

    }

    @Before("mysqlAspect()")
    public void mysql(){
        log.info("切换到mysql的数据源");
        DynamicDataSourceContextHolder.setDataSourceType(DataSourceTypeEnum.mysql.getValue());
    }

    @Before("oracleAspect()")
    public void oracle(){
        log.info("切换到oracle的数据源");
        DynamicDataSourceContextHolder.setDataSourceType(DataSourceTypeEnum.oracle.getValue());
    }

}
