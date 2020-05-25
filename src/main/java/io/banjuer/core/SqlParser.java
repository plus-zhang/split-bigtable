package io.banjuer.core;

import io.banjuer.exception.SqlParseException;
import io.banjuer.util.CharacterUtils;
import io.banjuer.util.EmptyUtils;

public class SqlParser {

    /**
     * 原始sql
     */
    private String sql;

    /**
     * 表名
     */
    private String table;

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
     * 分表
     */
    private String shardValue;

    /**
     * 格式化的sql
     */
    private String formatted;

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

    private void formatLower() {
        char[] cs = sql.toCharArray();
        char last = cs[0];
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < cs.length; i++) {
            char cur = cs[i];
            if (cur == ' ' && last == ' ') {
                continue;
            }
            last = cur;
            sb.append(CharacterUtils.toLower(cur));
        }
        this.formatted = sb.toString();
    }

    public static void main(String[] args) {
        String sql = " Select * from  T_data where  Log =  1";
        String formatted = SqlParser.parse(sql).getFormatted();
        System.out.println(formatted);
    }

}
