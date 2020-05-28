package io.banjuer.core.work;

import io.banjuer.helper.BaseJdbcTemplate;

import java.util.List;

/**
 * after select:
 * 1. drop temp table
 * 2. cache result
 */
public class CleanWorker implements Runnable {

    private String sqlKey;
    private BaseJdbcTemplate template;
    private List<Object[]> rows;

    public CleanWorker(String sqlKey, BaseJdbcTemplate template, List<Object[]> rows) {
        this.sqlKey = sqlKey;
        this.template = template;
        this.rows = rows;
    }

    @Override
    public void run() {

    }
}
