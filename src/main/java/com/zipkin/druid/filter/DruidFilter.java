package com.zipkin.druid.filter;

import brave.Span;
import brave.Tracer;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import java.sql.SQLException;
import java.util.Properties;

public class DruidFilter extends FilterEventAdapter {
    Tracer tracer;
    public DruidFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public DruidPooledConnection dataSource_getConnection(FilterChain chain, DruidDataSource dataSource, long maxWaitMillis) throws SQLException {
        Span span=tracer.nextSpan().name("druid.connectPool.connect").start();
        DruidPooledConnection druidPooledConnection=super.dataSource_getConnection(chain, dataSource, maxWaitMillis);
        span.tag("druid.activeCount",String.valueOf(dataSource.getActiveCount()));
        span.finish();
        return druidPooledConnection;
    }
//    @Override
//    public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
//        connection_connectBefore(chain, info);
//        Span span=tracer.nextSpan().name("druid.connect").start();
//        ConnectionProxy connection = super.connection_connect(chain, info);
//        span.finish();
//        connection_connectAfter(connection);
//
//        return connection;
//    }
}
