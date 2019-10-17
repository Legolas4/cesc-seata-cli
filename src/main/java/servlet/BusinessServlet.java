package servlet;

import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.core.model.GlobalStatus;
import io.seata.rm.RMClient;
import io.seata.tm.TMClient;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/*
* fixme
* 1.数据源如何代理
* 2.undo_log如何处理
* 3.如何回滚
* 4.代理事务？
* */

/**
 * todo
 *
 * @author wangxin
 * @version 2019/9/11 22:27
 */
public class BusinessServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessServlet.class);

    @Override
    public void init() {
        String applicationId = "business-service";
        String txServiceGroup = "my_test_tx_group";
        TMClient.init(applicationId, txServiceGroup);
        RMClient.init(applicationId, txServiceGroup);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        GlobalTransaction gtx = GlobalTransactionContext.getCurrentOrCreate();
        RootContext.bindGlobalLockFlag();

        try {
            gtx.begin(100000, "business");
        } catch (TransactionException e) {
            e.printStackTrace();
        }


        String storageUrl = "http://127.0.0.1:8081/storage";
//        String orderUrl = "http://127.0.0.1:8081/order";
        String accountUrl = "http://127.0.0.1:8081/account";

        String includeUrl = "http://127.0.0.1:8081/include";

//        Map<String, String> headers = new HashMap<>();
//        headers.put("TX_XID", RootContext.getXID());

        try {
//            String storageResult = util.HttpUtil.sendGet(storageUrl, "");
////            String orderResult = util.HttpUtil.sendGet(orderUrl, "");
//            String accountResult = util.HttpUtil.sendGet(accountUrl, "");

            String includeResult = util.HttpUtil.sendGet(includeUrl, "");
            gtx.commit();

        } catch (Exception e) {
            try {
                gtx.rollback();
            } catch (TransactionException e1) {
                e1.printStackTrace();
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
