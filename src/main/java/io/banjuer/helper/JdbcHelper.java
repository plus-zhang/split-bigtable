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

    public MySQLJdbcTemplate getManageJdbc() {
        return getUserDataJdbc(ProjectConst.MANAGE_DATABASE);
    }

    public MySQLJdbcTemplate getManageDataJdbc() {
        return getUserDataJdbc(ProjectConst.MANAGE_DATA_DATABASE);
    }

    public MySQLJdbcTemplate getUserDataJdbc(String dbName) {
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
