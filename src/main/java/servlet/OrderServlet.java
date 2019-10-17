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
import java.io.IOException;
import java.io.Writer;
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
public class OrderServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServlet.class);

    @Override
    public void init() {
//        String applicationId = "order-service";
//        String txServiceGroup = "my_test_tx_group";
//        TMClient.init(applicationId, txServiceGroup);
//        RMClient.init(applicationId, txServiceGroup);
//
//        RmRpcClient rmRpcClient = RmRpcClient.getInstance(applicationId,txServiceGroup);
//        System.out.println(rmRpcClient);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)  throws ServletException,IOException{

        ConnectionProxy connectionProxy = null;
        PreparedStatementProxy preparedStatementProxy = null;
        try {

            DataSourceProxy dataSourceProxy = (DataSourceProxy) DataSourceUtil.getDataSource("order");
            connectionProxy = dataSourceProxy.getConnection();

            String sql = "insert into order_tbl (id,user_id,commodity_code,count,money) values (?, ?, ?, ?, ?)";

            String uuid = UUID.randomUUID().toString().replace("-", "");
            preparedStatementProxy = (PreparedStatementProxy)connectionProxy.prepareStatement(sql);
            preparedStatementProxy.setString(1, uuid);
            preparedStatementProxy.setString(2, "user_id");
            preparedStatementProxy.setString(3, "commodity_code");
            preparedStatementProxy.setInt(4, 1);
            preparedStatementProxy.setInt(5, 1);


            LOGGER.info("=====Order-XID:【{}】", RootContext.getXID());

            preparedStatementProxy.execute();

        } catch (SQLException e) {
            throw new RuntimeException("order,,,,,,,,,,,,,,,,");
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
