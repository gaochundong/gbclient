package ai.sangmado.gbclient.jt808.client;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * JT808 业务消息处理器
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class JT808MessageHandler<I extends JT808MessagePacket, O extends JT808MessagePacket> extends MessageToMessageDecoder<JT808MessagePacket> {
    private final ISpecificationContext ctx;
    private Connection<I, O> establishedConnection = null;

    public JT808MessageHandler(ISpecificationContext ctx) {
        this.ctx = ctx;
    }

    public void notifyConnectionConnected(Connection<I, O> connection) {
        // 新的连接建立
        establishedConnection = connection;
    }

    public void notifyConnectionClosed(Connection<I, O> connection) {
        // 连接已关闭
        establishedConnection = null;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, JT808MessagePacket msg, List<Object> out) throws Exception {
        String connectionId = ctx.channel().id().asLongText();
        System.out.println(connectionId);
        out.add(msg);
    }
}