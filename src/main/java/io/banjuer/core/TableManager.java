package io.banjuer.core;

import io.banjuer.config.ProjectConst;
import io.banjuer.config.em.SplitType;
import io.banjuer.exception.SqlParseException;
import io.banjuer.helper.JdbcHelper;
import io.banjuer.helper.MySQLJdbcTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableManager {

    public static class TableDesc {

        TableDesc(String tableName, String shardField, String shardType, String[] shardValues, SplitType splitType) {
            this.tableName = tableName;
            this.shardField = shardField;
            this.shardType = shardType;
            this.shardValues = shardValues;
            this.splitType = splitType;
        }

        String tableName;

        /**
         * 分表字段
         */
        protected String shardField;

        /**
         * 分表方式
         */
        protected SplitType splitType;

        /**
         * 分表字段类型
         */
        protected String shardType;

        /**
         * 分片值
         */
        protected String[] shardValues;

    }

    private static final Map<String, TableDesc> MANAGER = new ConcurrentHashMap<>();

    public static TableDesc getDesc(String tableName) {
        TableDesc tableDesc = MANAGER.get(tableName);
        if (tableDesc == null) {
            initTable(tableName);
        }
        return MANAGER.get(tableName);
    }

    private static void initTable(String tableName) {
        MySQLJdbcTemplate template = JdbcHelper.INSTANCE.getManageJdbc();
        template.executeQuery(String.format("select * from %s where table_name=?", ProjectConst.T_OL_IMPORT), new Object[]{tableName}, rs -> {
            int columnCount = rs.getMetaData().getColumnCount();
            if (columnCount != 1) {
                throw new SqlParseException("table: " + tableName + " not managed");
            }
            if (rs.next()) {
                String shardField = rs.getString(4);
                String shardType= rs.getString(5);
                String[] values = rs.getString(6).split(",");
                SplitType splitType = SplitType.valueOf(rs.getString(3));
                MANAGER.put(tableName, new TableDesc(tableName, shardField, shardType, values, splitType));
            }
        });
    }

}
