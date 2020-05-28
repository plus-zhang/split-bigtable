package io.banjuer.core;

import io.banjuer.config.ProjectConst;
import io.banjuer.config.em.SqlType;

/**
 * @author gcs
 */
public class SelectParser extends SqlParser {

    private String selectSegm;

    private String forceSegm;
    
    private String whereSegm;

    private String groupSegm;

    private String orderSegm;

    private String limitSegm;

    /**
     * 待占位符的格式化sql
     */
    private String lowerSqlHolder;

    /**
     * 最终结果sql
     */
    private String reduceSql;

    public SelectParser(String sql) {
        super(sql);
        selectInit();
        tableDesc = TableManager.getDesc(tableName);
    }

    private void selectInit() {
        String[] ss = lowerSql.split(" ");
        this.sqlType = SqlType.valueOf(ss[0]);
        String lastKey = "select";
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < ss.length; i ++) {
            switch (ss[i]) {
                case "from" -> {
                    selectSegm = sb.toString().trim();
                    sb.setLength(0);
                    lastKey = "from";
                }
                case "force" -> {
                    tableName = sb.toString().trim();
                    sb.setLength(0);
                    lastKey = "force";
                }
                case "where" -> {
                    if (!"force".equals(lastKey)) {
                        tableName = sb.toString().trim();
                    } else {
                        forceSegm = sb.toString().trim();
                    }
                    sb.setLength(0);
                    lastKey = "where";
                }
                case "group" -> {
                    switch (lastKey) {
                        case "force" -> forceSegm = sb.toString().trim();
                        case "where" -> whereSegm = sb.toString().trim();
                        default -> tableName = sb.toString();
                    }
                    sb.setLength(0);
                    lastKey = "group";
                }
                case "order" -> {
                    switch (lastKey) {
                        case "force" -> forceSegm = sb.toString().trim();
                        case "where" -> whereSegm = sb.toString().trim();
                        case "group" -> groupSegm = sb.toString().trim();
                        default -> tableName = sb.toString().trim();
                    }
                    sb.setLength(0);
                    lastKey = "order";
                }
                case "limit" -> {
                    switch (lastKey) {
                        case "force" -> forceSegm = sb.toString().trim();
                        case "where" -> whereSegm = sb.toString().trim();
                        case "group" -> groupSegm = sb.toString().trim();
                        case "order" -> orderSegm = sb.toString().trim();
                        default -> tableName = sb.toString().trim();
                    }
                    sb.setLength(0);
                    lastKey = "limit";
                }
                default -> sb.append(' ').append(ss[i]);
            }
        }
        if (sb.length() != 0) {
            switch (lastKey) {
                case "force" -> forceSegm = sb.toString().trim();
                case "where" -> whereSegm = sb.toString().trim();
                case "group" -> groupSegm = sb.toString().trim();
                case "order" -> orderSegm = sb.toString().trim();
                case "limit" -> limitSegm = sb.toString().trim();
                default -> tableName = sb.toString().trim();
            }
        }
    }

    /**
     * 去除where语句 TODO
     */
    public String getReduceSql() {
        if (reduceSql == null) {
            this.reduceSql = this.lowerSql.replace(tableName, sqlKey);
        }
        return reduceSql;
    }

    public String getlowerSqlHolder() {
        if (lowerSqlHolder == null) {
            this.lowerSqlHolder = lowerSql.replace(getTableName(), ProjectConst.SHARD_TABLE_TAIL + ProjectConst.SHARD_HOLDER);
        }
        return lowerSqlHolder;
    }

    public static String formatShardSql(SelectParser parse, String shardValue) {
        return "insert into " + parse.getSqlKey() + parse.getlowerSqlHolder().replace(ProjectConst.SHARD_HOLDER, shardValue);
    }

    public String getTableName() {
        return this.tableName;
    }

    /**
     * 当查询条件指定了分片值, 只查对应分片表, 如果未指定, 则所有分片全查
     */
    public String[] getReferShardValues() {
        // if (this.shardValues == null) {
        //     if (this.getAfterWhere().contains(this.getShardField())) {
        //         String afterShard = this.afterWhere.split( shardField)[1].trim();
        //         if (afterShard.startsWith("in")) {
        //             this.shardValues = getInValues(afterShard);
        //         } else if (afterShard.startsWith("=")) {
        //             this.shardValues = getEqValues(afterShard);
        //         } else {
        //             throw new SqlParseException("unsupport operation: " + afterShard);
        //         }
        //     } else {
        //         this.shardValues = tableDesc.shardValues;
        //     }
        // }
        // return shardValues;
        return null;
    }

    /**
     * in ('a', 'b',...)
     */
    private String[] getInValues(String sql) {
        String[] strings = sql.substring(sql.indexOf("(") + 1, sql.indexOf(")")).split(",");
        for (int i = 0; i < strings.length; i++) {
            strings[i] = strings[i].trim();
        }
        return strings;
    }

    /**
     * =1
     */
    private String[] getEqValues(String sql) {
        String s = sql.split("=")[1].trim().split(" ")[0];
        return new String[]{s};
    }

    @Override
    public String toString() {
        return "SelectParser{" +
                "selectSegm='" + selectSegm + '\'' +
                ", tableName='" + tableName + '\'' +
                ", forceSegm='" + forceSegm + '\'' +
                ", whereSegm='" + whereSegm + '\'' +
                ", groupSegm='" + groupSegm + '\'' +
                ", orderSegm='" + orderSegm + '\'' +
                ", limitSegm='" + limitSegm + '\'' +
                ", lowerSqlHolder='" + lowerSqlHolder + '\'' +
                ", reduceSql='" + reduceSql + '\'' +
                ", lowerSql='" + lowerSql + '\'' +
                ", sqlType=" + sqlType +
                '}';
    }

    public static void main(String[] args) {
        String sql = " SELECT * FROM DATA  limit 1";
        SelectParser select = new SelectParser(sql);
        System.out.println(select.toString());
    }

}
