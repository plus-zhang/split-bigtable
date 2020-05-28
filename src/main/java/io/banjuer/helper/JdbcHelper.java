package io.banjuer.helper;

import io.banjuer.config.ProjectConst;

import java.util.HashMap;
import java.util.Map;

public enum JdbcHelper {

    /**
     * conn instance
     */
    INSTANCE;

    private final Map<String, Object> templateMap = new HashMap<>();

    JdbcHelper() {
    }

    public MySQLJdbcTemplate getMysqlJdbc() {
        Object target = templateMap.get("mysql." + ProjectConst.DATABASE);
        if (target == null) {
            synchronized (this) {
                if (target == null) {
                    target = new MySQLJdbcTemplate(ProjectConst.DATABASE);
                    templateMap.put("mysql." + ProjectConst.DATABASE, target);
                }
            }
        }
        return (MySQLJdbcTemplate) target;
    }

    public MySQLJdbcTemplate getMysqlJdbc(String dbName) {
        Object target = templateMap.get("mysql." + dbName);
        if (target == null) {
            synchronized (this) {
                if (target == null) {
                    target = new MySQLJdbcTemplate(dbName);
                    templateMap.put("mysql." + dbName, target);
                }
            }
        }
        return (MySQLJdbcTemplate) target;
    }

}
