package ai.sangmado.gbclient.common.client;

import ai.sangmado.gbclient.common.channel.Connection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 连接生命周期管理器
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
public class ConnectionLifecycleHandler<I, O> extends ChannelInboundHandlerAdapter {

    private Connection<I, O> connection;

    void setConnection(Connection<I, O> newConnection) {
        if (!newConnection.getChannel().isRegistered()) {
            connection.close();
        } else {
            connection = newConnection;
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (connection != null) {
            connection.close();
        }
        super.channelUnregistered(ctx);
    }
}