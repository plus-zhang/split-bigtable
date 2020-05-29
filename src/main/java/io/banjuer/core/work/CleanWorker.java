package io.banjuer.core.work;

import com.alibaba.fastjson.JSON;
import io.banjuer.config.ProjectConst;
import io.banjuer.helper.JdbcHelper;
import io.banjuer.helper.MySQLJdbcTemplate;

import java.util.Date;
import java.util.List;

/**
 * after select:
 * 1. cache result
 * 2. drop temp table
 */
public class CleanWorker implements Runnable {

    private String sqlKey;
    private String tempTable;
    private List<Object[]> rows;

    public CleanWorker(String sqlKey, String tempTable, List<Object[]> rows) {
        this.sqlKey = sqlKey;
        this.tempTable = tempTable;
        this.rows = rows;
    }

    @Override
    public void run() {
        MySQLJdbcTemplate manageJdbc = JdbcHelper.INSTANCE.getManageJdbc();
        manageJdbc.executeUpdate("insert into " + ProjectConst.T_CACHE + " values(?,?,?,?,?)", new Object[]{sqlKey, 0, JSON.toJSON(rows), new Date(), new Date()});
        manageJdbc.executeDDL("drop table " + ProjectConst.MANAGE_DATA_DATABASE + "." + tempTable);
    }

}
