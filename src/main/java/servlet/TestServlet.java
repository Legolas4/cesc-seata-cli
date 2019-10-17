package servlet;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.core.model.GlobalStatus;
import io.seata.rm.RMClient;
import io.seata.tm.TMClient;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import io.seata.tm.api.TransactionalExecutor;
import io.seata.tm.api.TransactionalTemplate;
import io.seata.tm.api.transaction.RollbackRule;
import io.seata.tm.api.transaction.TransactionInfo;
import org.apache.http.impl.client.HttpClients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Result;
import util.SendRequest;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


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
public class TestServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestServlet.class);

    @Override
    public void init() {
        String applicationId = "test-service";
        String txServiceGroup = "my_test_tx_group";
        TMClient.init(applicationId, txServiceGroup);
        RMClient.init(applicationId, txServiceGroup);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        String storageUrl = "http://127.0.0.1:8081/storage";
        String orderUrl = "http://127.0.0.1:8081/order";


        TransactionalTemplate transactionalTemplate = new TransactionalTemplate();
        final MyBusinessException bizException = new MyBusinessException("mock bizException");

        try {
            transactionalTemplate.execute(new TransactionalExecutor() {
                @Override
                public Object execute() throws Throwable {

                    Map<String, String> headers = new HashMap<>();
                    headers.put("TX_XID", RootContext.getXID());


                    // todo 如何通过判断异常来确定全局事务是回滚还是提交？？？
                    // OKHttpclient osp-ocr-client
//                    Result storageResult = SendRequest.sendGet(storageUrl, headers, null, "");
//                    Result orderResult = SendRequest.sendGet(orderUrl, headers, null, "");

//                    HttpRequest httpRequest = new DefaultHttpRequest()
                    String storageResult = util.HttpUtil.sendGet(storageUrl, "");
                    String orderResult = util.HttpUtil.sendGet(orderUrl, "");

                    System.out.println("【storageResult】 " + storageResult);
                    System.out.println("【orderResult】 " + orderResult);

                    // 这里是直接写了个异常
//                    throw bizException;
                    return null;
                }

                @Override
                public TransactionInfo getTransactionInfo() {
                    TransactionInfo transactionInfo = new TransactionInfo();
                    transactionInfo.setTimeOut(TransactionInfo.DEFAULT_TIME_OUT);
                    transactionInfo.setName("cesc");

                    Set<RollbackRule> rollbackRules = new HashSet<>();

                    RollbackRule rollbackRule = new RollbackRule("exceptionName");
                    rollbackRules.add(rollbackRule);
                    transactionInfo.setRollbackRules(rollbackRules);
                    return transactionInfo;
                }
            });
        } catch (TransactionalExecutor.ExecutionException e) {
            TransactionalExecutor.Code code = e.getCode();
            if (code == TransactionalExecutor.Code.RollbackDone) {
                Throwable businessEx = e.getOriginalException();
                LOGGER.info("code == TransactionalExecutor.Code.RollbackDone");
                if (businessEx instanceof MyBusinessException) {
//                    Assertions.assertEquals(((MyBusinessException) businessEx).getBusinessErrorCode(),
//                            bizException.businessErrorCode);
                }
            } else {
                LOGGER.info("code != TransactionalExecutor.Code.RollbackDone");
//                Assertions.assertFalse(false, "Not expected," + e.getMessage());

            }
        } catch (IOException e) {

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public void destroy() {

    }

    private static class MyBusinessException extends Exception {

        private String businessErrorCode;

        /**
         * Gets business error code.
         *
         * @return the business error code
         */
        public String getBusinessErrorCode() {
            return businessErrorCode;
        }

        /**
         * Sets business error code.
         *
         * @param businessErrorCode the business error code
         */
        public void setBusinessErrorCode(String businessErrorCode) {
            this.businessErrorCode = businessErrorCode;
        }

        /**
         * Instantiates a new My business exception.
         *
         * @param businessErrorCode the business error code
         */
        public MyBusinessException(String businessErrorCode) {
            this.businessErrorCode = businessErrorCode;
        }
    }


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        super.service(req, res);
    }
}
