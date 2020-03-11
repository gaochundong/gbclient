package ai.sangmado.gbclient.common.client;

import ai.sangmado.gbclient.common.channel.Connection;
import io.netty.channel.Channel;

/**
 * 客户端连接工厂
 */
public interface ClientConnectionFactory<I, O> {

    Connection<I, O> newConnection(Channel channel);
}