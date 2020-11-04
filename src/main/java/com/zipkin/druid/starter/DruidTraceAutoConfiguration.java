package com.zipkin.druid.starter;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.zipkin.druid.filter.DruidFilter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@Configuration
@ConditionalOnClass({DruidDataSource.class})
@EnableConfigurationProperties
public class DruidTraceAutoConfiguration {
    @Autowired(required = false)
    List<DruidDataSource> druidDataSourceList;
    @Resource
    Tracer tracer;

    @Bean
    public DruidFilter druidFilter(){
        DruidFilter druidFilter=new DruidFilter(tracer);
        return druidFilter;
    }
    @PostConstruct
    public void buildFilter() throws BeansException {
        if(CollectionUtils.isEmpty(druidDataSourceList)){
            return;
        }
        List<Filter> filters=new ArrayList<Filter>();
        filters.add(druidFilter());
        for (DruidDataSource druidDataSource:druidDataSourceList) {
            druidDataSource.setProxyFilters(filters);
        }
    }
    public void addDruidDataSourceFilter(DruidDataSource dataSource){
        if(druidDataSourceList!=null&&druidDataSourceList.contains(dataSource)){
            return;
        }
        List<Filter> filters=new ArrayList<Filter>();
        filters.add(druidFilter());
        dataSource.setProxyFilters(filters);

    }

}
