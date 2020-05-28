package io.banjuer.core;

import io.banjuer.config.ProjectConst;
import io.banjuer.config.em.SqlType;
import io.banjuer.exception.SqlParseException;

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
     * 执行的第一条sql, 作用:
     * 1. sql检查提前, 避免同时很多条异步查询报错
     * 2. 提前建表, 后续sql可以通过select into 插入临时表
     */
    private String select0;

    /**
     * 临时表
     */
    private String tempTable;

    /**
     * 最终结果sql
     */
    private String reduceSql;

    public SelectParser(String sql) {
        super(sql);
        selectInit();
        tableDesc = TableManager.getDesc(tableName);
        tempTable = tableName + ProjectConst.SHARD_TABLE_TAIL + getSqlKey();
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
     * 去除where语句
     */
    public String getReduceSql() {
        if (reduceSql == null) {
            this.reduceSql = "select " + selectSegm + " from " + tempTable
                    + (groupSegm == null ? "" : " group " +groupSegm) + (orderSegm == null ? "" : " order " + orderSegm)
                    + (limitSegm == null ? "" : " limit " + limitSegm);
        }
        return reduceSql;
    }

    public String getSelect0() {
        if (select0 == null)
            select0 = "create table " + tempTable + "(" + lowerSql.replace(tableName, shardTable(referShardValues[0])) + ")";
        return select0;
    }

    public String getSelect(String shardValue) {
        return "insert into " + tempTable + " " + lowerSql.replace(tableName, shardTable(shardValue));
    }

    private String shardTable(String shardValue) {
        return tableName + ProjectConst.SHARD_TABLE_TAIL + shardValue;
    }

    public String getTableName() {
        return this.tableName;
    }

    public String getTempTable() {
        return tempTable;
    }

    /**
     * 当查询条件指定了分片值, 只查对应分片表, 如果未指定, 则所有分片全查
     */
    public String[] getReferShardValues() {
        if (this.referShardValues == null) {
            if (whereSegm.contains(this.getShardField())) {
                String afterShard = whereSegm.split( getShardField())[1].trim();
                if (afterShard.startsWith("in")) {
                    this.referShardValues = getInValues(afterShard);
                } else if (afterShard.startsWith("=")) {
                    this.referShardValues = getEqValues(afterShard);
                } else {
                    throw new SqlParseException("unsupport operation: " + afterShard);
                }
            } else {
                this.referShardValues = tableDesc.shardValues;
            }
        }
        return referShardValues;
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
