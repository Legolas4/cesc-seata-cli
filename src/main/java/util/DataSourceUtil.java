package util;

import com.alibaba.druid.pool.DruidDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import io.seata.rm.datasource.DataSourceProxy;


import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import oracle.jdbc.pool.OracleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The type Data source util.
 *
 * @author jimin.jm @alibaba-inc.com
 * @date 2019 /08/21
 */
public class DataSourceUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceUtil.class);

    /**
     * The constant JDBC_PRO_PATH.
     */
    public static final String JDBC_PRO_PATH = "jdbc.properties";
    /**
     * The constant DATA_SOURCE_MAP.
     */
    public static final ConcurrentMap<String, DataSource> DATA_SOURCE_MAP = new ConcurrentHashMap<>();

    /**
     * Gets data source.
     *
     * @param name the name
     * @return the data source
     */
//    public static DataSource getDataSource(String name) {
//        String driverKey = "jdbc." + name + ".driver";
//        String urlKey = "jdbc." + name + ".url";
//        String userNameKey = "jdbc." + name + ".username";
//        String pwdKey = "jdbc." + name + ".password";
//
//        LOGGER.info("【使用DruidDataSource数据源】");
//
//        DataSource dataSource = new DruidDataSource();
//        ((DruidDataSource)dataSource).setDriverClassName(PropertiesUtil.getPropertieValue(JDBC_PRO_PATH, driverKey));
//        ((DruidDataSource)dataSource).setUrl(PropertiesUtil.getPropertieValue(JDBC_PRO_PATH, urlKey));
//        ((DruidDataSource)dataSource).setUsername(PropertiesUtil.getPropertieValue(JDBC_PRO_PATH, userNameKey));
//        ((DruidDataSource)dataSource).setPassword(PropertiesUtil.getPropertieValue(JDBC_PRO_PATH, pwdKey));
//
//        return new DataSourceProxy(dataSource);
//    }

    /**
     * Gets data source.
     *
     * @param name the name
     * @return the data source
     */
//    public static DataSource getDataSource(String name) {
//        String urlKey = "jdbc." + name + ".url";
//        String userNameKey = "jdbc." + name + ".username";
//        String pwdKey = "jdbc." + name + ".password";
//
//        DataSource dataSource = new MysqlDataSource();
//        ((MysqlDataSource)dataSource).setUrl(PropertiesUtil.getPropertieValue(JDBC_PRO_PATH, urlKey));
//        ((MysqlDataSource)dataSource).setUser(PropertiesUtil.getPropertieValue(JDBC_PRO_PATH, userNameKey));
//        ((MysqlDataSource)dataSource).setPassword(PropertiesUtil.getPropertieValue(JDBC_PRO_PATH, pwdKey));
//
//        LOGGER.info("【使用MysqlDataSource数据源】");
//        return new DataSourceProxy(dataSource);
//    }

    public static DataSource getDataSource(String name) {
        String urlKey = "jdbc." + name + ".url";
        String userNameKey = "jdbc." + name + ".username";
        String pwdKey = "jdbc." + name + ".password";

        DataSource dataSource = null;
        try {
            dataSource = new OracleDataSource();
//            dataSource = new SQLServerDataSource();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ((OracleDataSource) dataSource).setURL(PropertiesUtil.getPropertieValue(JDBC_PRO_PATH, urlKey));
        ((OracleDataSource) dataSource).setUser(PropertiesUtil.getPropertieValue(JDBC_PRO_PATH, userNameKey));
        ((OracleDataSource) dataSource).setPassword(PropertiesUtil.getPropertieValue(JDBC_PRO_PATH, pwdKey));

        LOGGER.info("【使用OracleDataSource数据源】");
        return new DataSourceProxy(dataSource);
    }

    /**
     * Gets connection.
     *
     * @param name the name
     * @return the connection
     * @throws SQLException the sql exception
     */
    public static Connection getConnection(String name) throws SQLException {
        DATA_SOURCE_MAP.putIfAbsent(name, getDataSource(name));
        return DATA_SOURCE_MAP.get(name).getConnection();
    }

    /**
     * Gets connection.
     *
     * @param name the name
     * @return the connection
     * @throws SQLException the sql exception
     */
    public static Connection getConnection2(String name) throws SQLException {
        String driverKey = "jdbc." + name + ".driver";
        String urlKey = "jdbc." + name + ".url";
        String userNameKey = "jdbc." + name + ".username";
        String pwdKey = "jdbc." + name + ".password";

//        DataSource mysqlDataSource = new MysqlDataSource();

        String driverClassName = PropertiesUtil.getPropertieValue(JDBC_PRO_PATH, driverKey);
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = PropertiesUtil.getPropertieValue(JDBC_PRO_PATH, urlKey);
        String username = PropertiesUtil.getPropertieValue(JDBC_PRO_PATH, userNameKey);
        String password = PropertiesUtil.getPropertieValue(JDBC_PRO_PATH, pwdKey);
        Connection connection = DriverManager.getConnection(url, username, password);
        return connection;
    }

    /**
     * Execute update int.
     *
     * @param name the name
     * @param sql  the sql
     * @return the int
     * @throws SQLException the sql exception
     */
    public static int executeUpdate(String name, String sql) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        int result = 0;
        try {
            connection = getConnection(name);
            statement = connection.createStatement();
            result = statement.executeUpdate(sql);

        } catch (SQLException exx) {
            //todo
            throw exx;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exx) {
            }
        }
        return result;
    }

    /**
     * Gets single result.
     *
     * @param name the name
     * @param sql  the sql
     * @return the single result
     * @throws SQLException the sql exception
     */
    public static String getSingleResult(String name, String sql) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        String result = null;
        try {
            connection = getConnection(name);
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            result = resultSet.getString(1);
        } catch (SQLException exx) {
            //todo
            throw exx;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exx) {
            }
        }
        return result;
    }
}
