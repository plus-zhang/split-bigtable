package io.banjuer.core;

import io.banjuer.config.em.SplitType;
import io.banjuer.config.em.SqlType;
import io.banjuer.util.CharacterUtils;
import io.banjuer.util.Md5Utils;

public abstract class SqlParser {

    protected String sql;

    protected String lowerSql;

    protected SqlType sqlType;

    protected String tableName;

    protected String[] referShardValues;

    /**
     * sql唯一标识(用于缓存), 同时也是临时表名
     */
    protected String sqlKey;

    protected TableManager.TableDesc tableDesc;

    /**
     * 调用者进行简单检查
     */
    protected SqlParser(String sql) {
        this.sql = sql;
        init();
    }

    private void init() {
        formatLower();
    }

    /**
     * 缓存key: table_md5(formatted)
     * table开头, 为了以后更新插入表, 删除缓存所用
     */
    public String getSqlKey() {
        if (sqlKey == null) {
            this.sqlKey = tableName+ "_" + Md5Utils.hash(lowerSql);
        }
        return sqlKey;
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
        this.lowerSql = sb.toString();
    }

    public SplitType getSplitType() {
        return tableDesc.splitType;
    }

    public SqlType getSqlType() {
        return sqlType;
    }

    public String getShardType() {
        return tableDesc.shardType;
    }

    public String getShardField() {
        return tableDesc.shardField;
    }

}
