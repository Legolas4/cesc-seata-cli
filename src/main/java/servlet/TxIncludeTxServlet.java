package servlet;


import io.seata.core.context.RootContext;
import io.seata.rm.datasource.ConnectionProxy;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.rm.datasource.PreparedStatementProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DataSourceUtil;
import util.HttpUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * 验证一个分支事务提交之前调用另一个分支事务情况
 *
 * @author wangxin
 * @version 2019/9/27 09:22:49
 */
public class TxIncludeTxServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxIncludeTxServlet.class);

    @Override
    public void init() {

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        ConnectionProxy connectionProxy = null;
        PreparedStatementProxy preparedStatementProxy = null;
//        PreparedStatementProxy preparedStatementProxy2 = null;



        DataSourceProxy dataSourceProxy = (DataSourceProxy) DataSourceUtil.getDataSource("account");

        try {


            connectionProxy = dataSourceProxy.getConnection();
            connectionProxy.setAutoCommit(false);
            String sql = "insert into account_tbl(id,user_id,money ) values(?,?,?)";
//            String sql2 = "insert into account_tbl(id,user_id,money ) values(?,?,?)";


            String uuid = UUID.randomUUID().toString().replace("-", "");
            preparedStatementProxy = (PreparedStatementProxy) connectionProxy.prepareStatement(sql);
            preparedStatementProxy.setString(1, uuid);
            preparedStatementProxy.setString(2, "commit-1 insert");
            preparedStatementProxy.setInt(3, 1000);

//            String uuid2 = UUID.randomUUID().toString().replace("-", "");
//            preparedStatementProxy2 = (PreparedStatementProxy) connectionProxy.prepareStatement(sql2);
//            preparedStatementProxy2.setString(1, uuid2);
//            preparedStatementProxy2.setString(2, "commit-2 insert");
//            preparedStatementProxy2.setInt(3, 100011);


            LOGGER.info("=====Account-XID-1:【{}】", RootContext.getXID());

            preparedStatementProxy.execute();
//            preparedStatementProxy2.execute();


            String storageUrl = "http://127.0.0.1:8081/storage";
//            Map<String, String> headers = new HashMap<>();
//            headers.put("TX_XID", RootContext.getXID());
            String storageResult = HttpUtil.sendGet(storageUrl, "");

            connectionProxy.commit();
            LOGGER.info("【---AccountServlet ---提交成功】");

        } catch (SQLException e) {
            LOGGER.error("提交事务出错{}",e);
            throw new RuntimeException("1111111111111111111");
        } catch (Exception e) {
            try {
                connectionProxy.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw new RuntimeException("2222222222222222222");
        } finally {
            try {
                connectionProxy.close();
                preparedStatementProxy.close();
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
