package util;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author wangxin
 * @version 2019/9/11 22:06
 */
public class DataSourceUtilss {

    public static void main(String[] args) {
        DruidDataSource druidDataSource = new DruidDataSource();
        DataSourceProxy dataSourceProxy = new DataSourceProxy(druidDataSource);
    }


    public static DruidDataSource getDruidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        // 设置连接池的名称
        druidDataSource.setName("");

//        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        druidDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/db_account?useSSL=false&serverTimezone=UTC");
//        druidDataSource.setUsername("root");
//        druidDataSource.setPassword("123456");

        druidDataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        druidDataSource.setUrl("jdbc:oracle:thin:@//127.0.0.1:1521/orcl");
        druidDataSource.setUsername("db_order");
        druidDataSource.setPassword("root");

        // 初始化时建立物理连接的个数，并发量不大时，可以设置为3
        // 初始化发生在显示调用init方法，或者第一次getConnection时
        druidDataSource.setInitialSize(4);

        // 最小连接数，跟initialSize的值保持一致
        druidDataSource.setMinIdle(4);

        // 最大连接数（根据数据库并发量设置最大连接数，还需要考虑集群节点个数）
        druidDataSource.setMaxActive(20);

        // todo获取连接超时时间（单位：ms）
        druidDataSource.setMaxWait(10000);

        // 是否缓存preparedStatement，也就是PSCache。
        druidDataSource.setPoolPreparedStatements(true);

        // 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。
        // maxOpenPreparedStatements是针对单个connection链接的
        druidDataSource.setMaxOpenPreparedStatements(100);

        // todo连接有效性检测时间(单位:ms)
        druidDataSource.setTimeBetweenEvictionRunsMillis(30000);

        // 获取连接检测，默认为关闭
        druidDataSource.setTestOnBorrow(false);

        // 归还连接检测，默认为关闭
        druidDataSource.setTestOnReturn(false);

        // 最小空闲时间(单位ms)，默认为30分钟
        druidDataSource.setMinEvictableIdleTimeMillis(1800000);

        // 在获取连接后，确定是否要进行连接空间时间的检查
        druidDataSource.setTestWhileIdle(true);

        // 2018年12月18日09:27:21

////         超过时间限制是否回收
//        druidDataSource.setRemoveAbandoned(true);
////         todo超时时间；单位为秒。180秒=3分钟
//        druidDataSource.setRemoveAbandonedTimeoutMillis(40000);
////         关闭abanded连接时输出错误日志
//        druidDataSource.setLogAbandoned(true);

//        StatFilter filter = new StatFilter();
//        filter.setSlowSqlMillis(3000);
//        filter.setLogSlowSql(true);
//
//        List<Filter> filters = new ArrayList<>();
//        filters.add(filter);
//
//        try {
//            druidDataSource.setFilters("stat,wall,log4j");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        druidDataSource.setProxyFilters(filters);
        return druidDataSource;
    }
}
