package io.banjuer.helper;


import com.alibaba.druid.pool.DruidDataSourceFactory;
import io.banjuer.config.CustomProperties;
import io.banjuer.exception.SqlRunnerException;
import io.banjuer.util.EmptyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;


/**
 * abstract jdbc operations template
 */
@Slf4j
public abstract class BaseJdbcTemplate {

    /**
     * jdbc配置参数：数据库类型.数据库名.参数名
     */
    private static final String JDBC_KEY = "jdbc.%s.%s.%s";
    private DataSource dataSource;

    protected String dbType;
    protected String dbName;

    /**
     * 获取数据库连接
     */
    public BaseJdbcTemplate(String dbType, String dbName) {
        this.dbType = dbType;
        this.dbName = dbName;
        try {
            boolean usePool = CustomProperties.jdbc.getBoolean(String.format(JDBC_KEY, dbType, dbName, "usePool"));
            if (usePool) {
                // 如果使用连接池则创建数据源
                Properties prop = new Properties();
                prop.setProperty("driverClassName", getJdbcProperty("driverClassName"));
                prop.setProperty("url", getJdbcProperty("url"));
                prop.setProperty("username", getJdbcProperty("username"));
                prop.setProperty("password", getJdbcProperty("password"));
                prop.setProperty("filters", CustomProperties.jdbc.getProperty("jdbc.pool.default.filters"));
                prop.setProperty("initialSize", CustomProperties.jdbc.getProperty("jdbc.pool.default.initialSize"));
                prop.setProperty("maxActive", CustomProperties.jdbc.getProperty("jdbc.pool.default.maxActive"));
                prop.setProperty("maxWait", CustomProperties.jdbc.getProperty("jdbc.pool.default.maxWait"));
                prop.setProperty("timeBetweenEvictionRunsMillis", CustomProperties.jdbc.getProperty("jdbc.pool.default.timeBetweenEvictionRunsMillis"));
                prop.setProperty("minEvictableIdleTimeMillis", CustomProperties.jdbc.getProperty("jdbc.pool.default.minEvictableIdleTimeMillis"));
                prop.setProperty("validationQuery", CustomProperties.jdbc.getProperty("jdbc.pool.default.validationQuery"));
                prop.setProperty("testWhileIdle", CustomProperties.jdbc.getProperty("jdbc.pool.default.testWhileIdle"));
                prop.setProperty("testOnBorrow", CustomProperties.jdbc.getProperty("jdbc.pool.default.testOnBorrow"));
                prop.setProperty("testOnReturn", CustomProperties.jdbc.getProperty("jdbc.pool.default.testOnReturn"));
                prop.setProperty("maxPoolPreparedStatementPerConnectionSize", CustomProperties.jdbc.getProperty("jdbc.pool.default.maxPoolPreparedStatementPerConnectionSize"));

                dataSource = DruidDataSourceFactory.createDataSource(prop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接
     */
    protected Connection getConnection() throws SQLException, ClassNotFoundException {
        if (null == dataSource) {
            Class.forName(getJdbcProperty("driverClassName"));
            return DriverManager.getConnection(getJdbcProperty("url")
                    , getJdbcProperty("username")
                    , getJdbcProperty("password"));
        } else {
            return dataSource.getConnection();
        }
    }

    /**
     * 获取指定数据库的jdbc属性
     */
    private String getJdbcProperty(String key) {
        return CustomProperties.jdbc.getProperty(String.format(JDBC_KEY, this.dbType, this.dbName, key));
    }

    /**
     * 设置参数
     */
    private void setParams(PreparedStatement pstmt, Object[] params) throws SQLException {
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
        }
    }

    /**
     * 执行增删改SQL语句
     */
    public int executeUpdate(String sql, Object[] params) {
        int rtn;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, params);
            rtn = pstmt.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            String param = EmptyUtils.isEmpty(params) ? "" : StringUtils.join(params, ",");
            log.error("执行sql:［" + sql + "］出错, 参数:［" + param + "］", e);
            throw new SqlRunnerException(e.getMessage());
        } finally {
            closeStatement(pstmt);
            closeConnection(conn);
        }
        return rtn;
    }

    /**
     * 执行DDL语句
     */
    public int executeDDL(String sql) throws SQLException {
        int rtn;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            rtn = stmt.executeUpdate(sql);
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("执行sql:［" + sql + "］出错 ", e);
            throw new SqlRunnerException(e.getMessage());
        } finally {
            closeStatement(stmt);
            closeConnection(conn);
        }
        return rtn;
    }

    /**
     * 执行查询SQL语句
     */
    public void executeQuery(String sql, Object[] params, QueryCallback callback) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, params);
            rs = pstmt.executeQuery();
            callback.process(rs);
        } catch (Exception e) {
            e.printStackTrace();
            String param = EmptyUtils.isEmpty(params) ? "" : StringUtils.join(params, ",");
            log.error("执行sql:［" + sql + "］出错, 参数:［" + param + "］", e);
            throw new SqlRunnerException(e.getMessage());
        } finally {
            closeStatement(pstmt);
            closeResultSet(rs);
            closeConnection(conn);
        }
    }

    /**
     * 批量执行SQL语句
     */
    public int[] executeBatch(String sql, List<Object[]> paramsList) {
        int[] rtn = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // 第一步：使用Connection对象，取消自动提交
            conn = getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            // 第二步：使用PreparedStatement.addBatch()方法加入批量的SQL参数
            if (paramsList != null && paramsList.size() > 0) {
                for (Object[] params : paramsList) {
                    for (int i = 0; i < params.length; i++) {
                        pstmt.setObject(i + 1, params[i]);
                    }
                    pstmt.addBatch();
                }
            }
            // 第三步：使用PreparedStatement.executeBatch()方法，执行批量的SQL语句
            rtn = pstmt.executeBatch();
            // 最后一步：使用Connection对象，提交批量的SQL语句
            conn.commit();
        } catch (Exception e) {
            throw new SqlRunnerException(e.getMessage());
        } finally {
            closeStatement(pstmt);
            closeConnection(conn);
        }
        return rtn;
    }

    /**
     * 静态内部类：查询回调接口
     */
    public interface QueryCallback {
        /**
         * 处理查询结果
         */
        void process(ResultSet rs) throws Exception;
    }

    /**
     * 资源关闭
     */
    protected void closeResultSet(ResultSet resultSet) {
        if (resultSet == null) {
            return;
        }
        try {
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void closeConnection(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void closeStatement(Statement statement) {
        if (statement == null) {
            return;
        }
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
