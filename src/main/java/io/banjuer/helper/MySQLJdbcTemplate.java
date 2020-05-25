package io.banjuer.helper;

public class MySQLJdbcTemplate extends BaseJdbcTemplate {
    public MySQLJdbcTemplate(String dbName) {
        super("mysql", dbName);
    }
}
