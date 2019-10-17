package config;

//@ConfigurationProperties("spring.cloud.alibaba.seata")
public class SeataProperties {
    private String txServiceGroup;

    public SeataProperties() {
    }

    public String getTxServiceGroup() {
        return this.txServiceGroup;
    }

    public void setTxServiceGroup(String txServiceGroup) {
        this.txServiceGroup = "my_test_tx_group";
    }
}
