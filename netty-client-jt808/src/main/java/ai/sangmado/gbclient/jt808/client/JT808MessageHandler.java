package ai.sangmado.gbclient.jt808.client;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbclient.jt808.client.utils.Jackson;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * JT808 业务消息处理器
 */
@Slf4j
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class JT808MessageHandler<I extends JT808MessagePacket, O extends JT808MessagePacket> extends MessageToMessageDecoder<JT808MessagePacket> {
    private final ISpecificationContext ctx;
    private Connection<I, O> establishedConnection = null;

    public JT808MessageHandler(ISpecificationContext ctx) {
        this.ctx = ctx;
    }

    public void notifyConnectionConnected(Connection<I, O> connection) {
        log.info("已与服务器建立连接, connectionId[{}]", connection.getConnectionId());
        establishedConnection = connection;
    }

    public void notifyConnectionClosed(Connection<I, O> connection) {
        log.info("已与服务器关闭连接, connectionId[{}]", connection.getConnectionId());
        establishedConnection = null;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, JT808MessagePacket msg, List<Object> out) throws Exception {
        String connectionId = ctx.channel().id().asLongText();

        String json = Jackson.toJsonPrettyString(msg);
        log.info("从服务器连接 [{}] 中接收到消息, 协议版本[{}], 消息ID[{}/{}]{}{}",
                connectionId,
                msg.getHeader().getProtocolVersion().getName(),
                msg.getHeader().getMessageId().getName(),
                msg.getHeader().getMessageId().getDescription(),
                System.lineSeparator(), json);
    }
}