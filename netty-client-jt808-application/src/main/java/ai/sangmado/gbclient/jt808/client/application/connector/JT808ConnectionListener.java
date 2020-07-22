package ai.sangmado.gbclient.jt808.client.application.connector;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbclient.common.client.event.ConnectionStatusChangedEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;

/**
 * JT808 服务器连接监听器
 */
@Slf4j
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class JT808ConnectionListener<I, O> implements Flow.Subscriber<ConnectionStatusChangedEvent<I, O>> {

    private volatile Connection<I, O> connection;

    public JT808ConnectionListener() {
    }

    public Connection<I, O> getEstablishedConnectionOrNull() {
        return connection;
    }

    public Connection<I, O> getEstablishedConnection() {
        Connection<I, O> copyConn = connection;
        if (copyConn == null)
            throw new IllegalStateException("连接未建立");
        if (!copyConn.isActive())
            throw new IllegalStateException("连接不活跃");
        return copyConn;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        // 无需流控, 订阅全部消息
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(ConnectionStatusChangedEvent<I, O> item) {
        switch (item.getStatus()) {
            case Connected:
                log.info("与服务器的连接建立成功, 连接状态[{}], 连接ID[{}]",
                        item.getStatus().name(), item.getConnection().getConnectionId());
                this.connection = item.getConnection();
                break;
            case Closed:
                log.info("与服务器的连接已经关闭, 连接状态[{}], 连接ID[{}]",
                        item.getStatus().name(), item.getConnection().getConnectionId());
                this.connection = null;
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
    }

    @Override
    public void onComplete() {
    }
}