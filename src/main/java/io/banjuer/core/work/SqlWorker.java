package io.banjuer.core.work;

import io.banjuer.config.em.SqlType;
import io.banjuer.helper.BaseJdbcTemplate;

import java.util.concurrent.CountDownLatch;

public abstract class SqlWorker implements Runnable {

    public SqlWorker(SqlType sqlType, CountDownLatch latch, BaseJdbcTemplate template, String sql) {
        this.sqlType = sqlType;
        this.latch = latch;
        this.template = template;
        this.sql = sql;
    }
    protected SqlType sqlType;
    protected CountDownLatch latch;
    protected BaseJdbcTemplate template;
    protected String sql;
}
