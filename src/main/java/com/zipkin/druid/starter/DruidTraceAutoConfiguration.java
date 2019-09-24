package com.zipkin.druid.starter;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.zipkin.druid.filter.DruidFilter;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.SleuthProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnClass({DruidDataSource.class})
@EnableConfigurationProperties
public class DruidTraceAutoConfiguration {
    @Resource
    DruidDataSource druidDataSource;
    @Resource
    Tracer tracer;
    @Bean
    public DruidFilter druidFilter(){
        return new DruidFilter(tracer);
    }
    @PostConstruct
    public void buildFilter() throws BeansException {
        List<Filter> filters=new ArrayList<Filter>();
        filters.add(druidFilter());
        druidDataSource.setProxyFilters(filters);
    }
}
