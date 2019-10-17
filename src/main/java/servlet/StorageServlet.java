package servlet;


import io.seata.core.context.RootContext;
import io.seata.core.rpc.netty.RmRpcClient;
import io.seata.rm.RMClient;
import io.seata.rm.datasource.ConnectionProxy;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.rm.datasource.PreparedStatementProxy;
import io.seata.tm.TMClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DataSourceUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;


/**
 * todo
 *
 * @author wangxin
 * @version 2019/9/17 16:29
 */
public class StorageServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageServlet.class);

    @Override
    public void init() {
//        String applicationId = "storage-service";
//        String txServiceGroup = "my_test_tx_group";
//        TMClient.init(applicationId, txServiceGroup);
//        RMClient.init(applicationId, txServiceGroup);
//
//        RmRpcClient rmRpcClient = RmRpcClient.getInstance(applicationId,txServiceGroup);
//        System.out.println(rmRpcClient);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        ConnectionProxy connectionProxy = null;
        PreparedStatementProxy preparedStatementProxy = null;

        try {


            DataSourceProxy dataSourceProxy = (DataSourceProxy) DataSourceUtil.getDataSource("storage");
            connectionProxy = dataSourceProxy.getConnection();
//            connectionProxy.setAutoCommit(false);

            String sql = "insert into storage_tbl(id,commodity_code,count ) values(?,?,?)";

            String uuid = UUID.randomUUID().toString().replace("-", "");
            preparedStatementProxy = (PreparedStatementProxy) connectionProxy.prepareStatement(sql);
            preparedStatementProxy.setString(1, uuid);
//            preparedStatementProxy.setString(2, "commodity_code 1111111111111");
            preparedStatementProxy.setString(2, "commodity_code");
            preparedStatementProxy.setInt(3, 1);

            LOGGER.info("=====Storage-XID:【{}】", RootContext.getXID());

            preparedStatementProxy.execute();
//            connectionProxy.commit();
            LOGGER.info("【---StorageServlet ---提交成功】");

        } catch (SQLException e) {
            try {
                connectionProxy.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw new RuntimeException("111");
        } finally {
            try {
                connectionProxy.close();
                preparedStatementProxy.close();
            } catch (SQLException e) {
                e.printStackTrace();
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
