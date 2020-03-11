package ai.sangmado.gbclient.common.client;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbclient.common.channel.UnpooledConnectionFactory;
import io.netty.channel.Channel;

/**
 * 客户端连接工厂实现
 */
public class UnpooledClientConnectionFactory<I, O> implements ClientConnectionFactory<I, O> {

    private final UnpooledConnectionFactory<I, O> delegate;

    public UnpooledClientConnectionFactory() {
        delegate = new UnpooledConnectionFactory<>();
    }

    @Override
    public Connection<I, O> newConnection(Channel channel) {
        return delegate.newConnection(channel);
    }
}