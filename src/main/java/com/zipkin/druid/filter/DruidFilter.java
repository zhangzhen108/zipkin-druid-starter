package com.zipkin.druid.filter;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.Properties;

public class DruidFilter extends FilterEventAdapter {
    Tracer tracer;
    private final Logger log = LoggerFactory.getLogger(DruidFilter.class);
    public DruidFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public DruidPooledConnection dataSource_getConnection(FilterChain chain, DruidDataSource dataSource, long maxWaitMillis) throws SQLException {
        Span span=tracer.createSpan("druid.connectPool.connect",tracer.getCurrentSpan());
        DruidPooledConnection druidPooledConnection=super.dataSource_getConnection(chain, dataSource, maxWaitMillis);
        span.tag("druid.activeCount",String.valueOf(dataSource.getActiveCount()));
        span.tag("druid.maxActive",String.valueOf(dataSource.getMaxActive()));
        span.tag("druid.maxIdle",String.valueOf(dataSource.getMaxIdle()));
        span.tag("druid.minIdle",String.valueOf(dataSource.getMinIdle()));
        tracer.close(span);
        log.info("druid.activeCount",String.valueOf(dataSource.getActiveCount()));
        return druidPooledConnection;
    }
//    @Override
//    public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
//        connection_connectBefore(chain, info);
//        tracer.createSpan("druid.connect",tracer.getCurrentSpan());
//        ConnectionProxy connection = super.connection_connect(chain, info);
//        tracer.close(tracer.getCurrentSpan());
//        connection_connectAfter(connection);
//
//        return connection;
//    }
}
