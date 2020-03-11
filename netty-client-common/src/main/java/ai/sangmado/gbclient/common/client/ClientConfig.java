package ai.sangmado.gbclient.common.client;

/**
 * 客户端参数配置
 */
public class ClientConfig implements Cloneable {

    public static final long NO_TIMEOUT = -1;
    private long readTimeoutInMillis = NO_TIMEOUT;

    protected ClientConfig() {
    }

    protected ClientConfig(ClientConfig config) {
        readTimeoutInMillis = config.readTimeoutInMillis;
    }

    public long getReadTimeoutInMillis() {
        return readTimeoutInMillis;
    }

    void setReadTimeoutInMillis(long readTimeoutInMillis) {
        this.readTimeoutInMillis = readTimeoutInMillis;
    }

    public boolean isReadTimeoutSet() {
        return NO_TIMEOUT != readTimeoutInMillis;
    }

    @Override
    public ClientConfig clone() throws CloneNotSupportedException {
        return (ClientConfig) super.clone();
    }
}