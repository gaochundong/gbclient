package ai.sangmado.gbclient.common.client;

import lombok.Getter;
import lombok.Setter;

/**
 * 客户端参数配置
 */
public class ClientConfig implements Cloneable {
    public static final int NO_TIMEOUT = -1;

    @Getter
    @Setter
    private int connectTimeoutInMillis = NO_TIMEOUT;
    @Getter
    @Setter
    private int readTimeoutInMillis = NO_TIMEOUT;

    public ClientConfig() {
    }

    public ClientConfig withConnectTimeoutInMillis(int connectTimeoutInMillis) {
        setConnectTimeoutInMillis(connectTimeoutInMillis);
        return this;
    }

    public ClientConfig withReadTimeoutInMillis(int readTimeoutInMillis) {
        setReadTimeoutInMillis(readTimeoutInMillis);
        return this;
    }

    public boolean isConnectTimeoutSet() {
        return NO_TIMEOUT != connectTimeoutInMillis;
    }

    public boolean isReadTimeoutSet() {
        return NO_TIMEOUT != readTimeoutInMillis;
    }

    @Override
    public ClientConfig clone() throws CloneNotSupportedException {
        return (ClientConfig) super.clone();
    }
}