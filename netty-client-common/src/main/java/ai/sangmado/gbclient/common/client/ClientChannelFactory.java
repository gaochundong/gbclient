package ai.sangmado.gbclient.common.client;

import io.netty.channel.ChannelFuture;

/**
 * 客户端通道工厂
 */
public interface ClientChannelFactory<I, O> {

    ChannelFuture connect(ServerInfo serverInfo);
}