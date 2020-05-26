package io.banjuer.core;

import io.banjuer.config.SplitType;
import io.banjuer.exception.SqlParseException;
import io.banjuer.util.CharacterUtils;
import io.banjuer.util.EmptyUtils;
import io.banjuer.util.Md5Utils;
import io.banjuer.util.StringUtils;

public class SqlParser {

    /**
     * 原始sql
     */
    private String sql;

    /**
     * 表名
     */
    private String tableName;

    /**
     * where后语句
     */
    private String afterWhere;

    /**
     * 查询的字段
     */
    private String[] fields;

    /**
     * sql唯一标识(用于缓存)
     */
    private String sqlKey;

    /**
     * 分表字段
     */
    private String shardField;

    /**
     * 分表字段类型
     */
    private String shardType;

    /**
     * 查询的分片值
     */
    private String[] shardValues;

    /**
     * 格式化的sql
     */
    private String formatted;

    /**
     * 分表方式
     */
    private SplitType splitType;

    private TableManager.TableDesc tableDesc;

    /**
     * 缓存key: table_md5(formatted)
     * table开头, 为了以后更新插入表, 删除缓存所用
     */
    public String getSqlKey() {
        if (sqlKey == null) {
            this.sqlKey = this.getTableName() + "_" + Md5Utils.hash(this.getFormatted());
        }
        return sqlKey;
    }

    public String[] getSelectFields() {
        if (fields == null) {
            String[] fields = this.getFormatted().split(" from ")[0].substring("select ".length()).trim().split(",");
            StringUtils.trim(fields);
            this.fields = fields;
        }
        return fields;
    }

    /**
     * 当查询条件指定了分片值, 只查对应分片表, 如果未指定, 则所有分片全查
     */
    public String[] getShardValues() {
        if (this.shardValues == null) {
            if (this.getAfterWhere().contains(this.getShardField())) {
                String afterShard = this.afterWhere.split( shardField)[1].trim();
                if (afterShard.startsWith("in")) {
                    this.shardValues = getInValues(afterShard);
                } else if (afterShard.startsWith("=")) {
                    this.shardValues = getEqValues(afterShard);
                } else {
                    throw new SqlParseException("unsupport operation: " + afterShard);
                }
            } else {
                this.shardValues = tableDesc.shardValues;
            }
        }
        return shardValues;
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

    public SplitType getSplitType() {
        if (this.splitType == null) {
            this.splitType = this.getTableDesc().splitType;
        }
        return splitType;
    }

    public String getShardType() {
        if (this.shardType == null) {
            this.shardType = this.getTableDesc().shardType;
        }
        return shardType;
    }

    public String getShardField() {
        if (this.shardField == null) {
            this.shardField = this.getTableDesc().shardField;
        }
        return shardField;
    }

    private TableManager.TableDesc getTableDesc() {
        if (tableDesc == null) {
            this.tableDesc = TableManager.getDesc(this.getTableName());
        }
        return tableDesc;
    }

    private SqlParser(String sql) {
        simpleCheck(sql);
        this.sql = sql.trim();
    }

    private void simpleCheck(String sql) {
        if (EmptyUtils.isEmpty(sql)) {
            throw new SqlParseException("empty sql");
        }
    }

    public static SqlParser parse(String sql) {
        return new SqlParser(sql);
    }

    public String getFormatted() {
        if (formatted == null) {
            this.formatLower();
        }
        return formatted;
    }

    /**
     * 去除sql中多余空格, 除条件外全部小写 => 缓存命中
     */
    private void formatLower() {
        char[] cs = sql.toCharArray();
        char last = ' ';
        boolean string = false;
        StringBuilder sb = new StringBuilder();
        for (char c : cs) {
            char cur = c;
            if (cur == ' ' && last == ' ') {
                continue;
            }
            last = cur;
            if (!string) {
                cur = CharacterUtils.toLower(cur);
            }
            if (cur == '\'') {
                string = !string;
            }
            sb.append(cur);
        }
        this.formatted = sb.toString();
    }

    public String getTableName() {
        if (this.tableName == null) {
            String formatted = this.getFormatted();
            String afterWhere = getAfterWhere();
            int end = "null".equals(afterWhere) ? formatted.length() : formatted.length() - afterWhere.length() - " where".length();
            this.tableName = formatted.substring(formatted.indexOf("from ") + "from ".length(), end);
        }
        return tableName;
    }

    private String getAfterWhere() {
        if (afterWhere == null) {
            String formatted = this.getFormatted();
            String[] wheres = formatted.split("where");
            if (wheres.length == 1) {
                this.afterWhere = "null";
            } else {
                this.afterWhere = wheres[1];
            }
        }
        return afterWhere;
    }

    public static void main(String[] args) {
        // String sql = " Select * from  T_data where    a =  'Yes' and b=3 OR  c=' he llo' ";
        // String sql = " Select * from  T_data";
        String sql = "=1 and";
        SqlParser parser = SqlParser.parse(sql);
        // String formatted = parser.getFormatted();
        // String tabName = parser.getTableName();

        System.out.println(sql.split("=")[1].trim().split(" ")[0]);
    }

}
