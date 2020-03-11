package ai.sangmado.gbclient.common.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

/**
 * 客户端通道工厂实现
 */
public class ClientChannelFactoryImpl<I, O> implements ClientChannelFactory<I, O> {

    protected final Bootstrap clientBootstrap;

    public ClientChannelFactoryImpl(Bootstrap clientBootstrap) {
        this.clientBootstrap = clientBootstrap;
    }

    @Override
    public ChannelFuture connect(ServerInfo serverInfo) {
        return clientBootstrap.connect(serverInfo.getHost(), serverInfo.getPort());
    }
}
