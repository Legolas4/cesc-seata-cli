package servlet;


import io.seata.core.context.RootContext;
import io.seata.rm.datasource.ConnectionProxy;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.rm.datasource.PreparedStatementProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DataSourceUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.UUID;


/**
 * 验证一个分支事务多个commit情况
 *
 * @author wangxin
 * @version 2019/9/27 09:22:49
 */
public class AccountServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServlet.class);

    @Override
    public void init() {

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        ConnectionProxy connectionProxy = null;
        PreparedStatementProxy preparedStatementProxy = null;

        ConnectionProxy connectionProxy2 = null;
        PreparedStatementProxy preparedStatementProxy2 = null;

        ConnectionProxy connectionProxy3 = null;
        PreparedStatementProxy preparedStatementProxy3 = null;

        DataSourceProxy dataSourceProxy = (DataSourceProxy) DataSourceUtil.getDataSource("account");

        try {


            connectionProxy = dataSourceProxy.getConnection();
            connectionProxy.setAutoCommit(false);
            String sql = "insert into account_tbl(id,user_id,money ) values(?,?,?)";

            String uuid = UUID.randomUUID().toString().replace("-", "");
            preparedStatementProxy = (PreparedStatementProxy) connectionProxy.prepareStatement(sql);
            preparedStatementProxy.setString(1, uuid);
            preparedStatementProxy.setString(2, "commit-1 insert");
            preparedStatementProxy.setInt(3, 1000);


            LOGGER.info("=====Account-XID-1:【{}】", RootContext.getXID());

            preparedStatementProxy.execute();
            connectionProxy.commit();
            LOGGER.info("【---AccountServlet 事务1---提交成功】");


            // 一、操作不同的数据
            connectionProxy2 = dataSourceProxy.getConnection();
            connectionProxy2.setAutoCommit(false);
            String sql2 = "update account_tbl set user_id =? where id = ?";
            String uuid2 = uuid;
            preparedStatementProxy2 = (PreparedStatementProxy) connectionProxy2.prepareStatement(sql2);
            preparedStatementProxy2.setString(1, "commit-2 update");
            preparedStatementProxy2.setString(2, uuid2);

            LOGGER.info("=====Account-XID-2:【{}】", RootContext.getXID());
            preparedStatementProxy2.execute();
            connectionProxy2.commit();
            LOGGER.info("【---AccountServlet 事务2---提交成功】");

            // 二、操作同一条数据

            connectionProxy3 = dataSourceProxy.getConnection();
            connectionProxy3.setAutoCommit(false);
            String sql3 = "update account_tbl set user_id =? where id = ?";

            String uuid3 = uuid;
            preparedStatementProxy3 = (PreparedStatementProxy) connectionProxy3.prepareStatement(sql3);
            preparedStatementProxy3.setString(1, "commit-3 update 超长字符串超过20就行");
//            preparedStatementProxy3.setString(1, "commit-3 update");

            // 同一条数据，也可以
            preparedStatementProxy3.setString(2, uuid3);

            LOGGER.info("=====Account-XID-3:【{}】", RootContext.getXID());
            preparedStatementProxy3.execute();
            connectionProxy3.commit();
            LOGGER.info("【---AccountServlet 事务3---提交成功】");

        } catch (SQLException e) {
            LOGGER.error("提交事务出错{}",e);
            throw new RuntimeException("111");
        } finally {
            try {
                connectionProxy.close();
                preparedStatementProxy.close();

                connectionProxy2.close();
                preparedStatementProxy2.close();

                connectionProxy3.close();
                preparedStatementProxy3.close();
            } catch (SQLException e) {
                LOGGER.error("关闭连接出错{}",e);
            }
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public void destroy() {

    }
}
