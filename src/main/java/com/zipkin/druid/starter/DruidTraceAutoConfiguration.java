package com.zipkin.druid.starter;

import brave.Tracer;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.zipkin.druid.filter.DruidFilter;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
