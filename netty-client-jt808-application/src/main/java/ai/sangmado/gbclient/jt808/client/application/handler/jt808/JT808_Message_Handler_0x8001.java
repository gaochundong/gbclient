package ai.sangmado.gbclient.jt808.client.application.handler.jt808;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbclient.jt808.client.application.handler.IJT808MessageHandler;
import ai.sangmado.gbclient.jt808.client.utils.Jackson;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import lombok.extern.slf4j.Slf4j;

/**
 * 平台通用应答
 */
@Slf4j
@SuppressWarnings({"FieldCanBeLocal"})
public class JT808_Message_Handler_0x8001<I extends JT808MessagePacket, O extends JT808MessagePacket>
        implements IJT808MessageHandler<I, O> {
    public static final JT808MessageId MESSAGE_ID = JT808MessageId.JT808_Message_0x0001;

    private final ISpecificationContext ctx;

    public JT808_Message_Handler_0x8001(ISpecificationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public JT808MessageId getMessageId() {
        return MESSAGE_ID;
    }

    @Override
    public void handle(Connection<I, O> connection, I message) {
        String json = Jackson.toJsonPrettyString(message);
        log.info("从服务器接收到消息, 消息ID[{}], 消息名称[{}], 协议版本[{}], 连接ID[{}]{}{}",
                message.getHeader().getMessageId().getName(),
                message.getHeader().getMessageId().getDescription(),
                message.getHeader().getProtocolVersion().getName(),
                connection.getConnectionId(),
                System.lineSeparator(), json);

        // 无需处理, 只打日志
    }
}