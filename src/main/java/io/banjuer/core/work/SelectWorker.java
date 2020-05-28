package io.banjuer.core.work;

import io.banjuer.config.em.SqlType;
import io.banjuer.helper.BaseJdbcTemplate;

import java.util.concurrent.CountDownLatch;

public class SelectWorker extends SqlWorker {

    public SelectWorker(CountDownLatch latch, BaseJdbcTemplate template, String sql) {
        super(SqlType.select, latch, template, sql);
    }

    @Override
    public void run() {
        try {
            template.executeUpdate(sql, null);
        } finally {
            latch.countDown();
        }
    }

}
