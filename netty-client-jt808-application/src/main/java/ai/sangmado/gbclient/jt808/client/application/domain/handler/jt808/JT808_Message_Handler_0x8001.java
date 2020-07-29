package ai.sangmado.gbclient.jt808.client.application.domain.handler.jt808;

import ai.sangmado.gbclient.jt808.client.JT808MessageHandlerContext;
import ai.sangmado.gbclient.jt808.client.application.domain.IJT808MessageHandler;
import ai.sangmado.gbclient.jt808.client.utils.Jackson;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import lombok.extern.slf4j.Slf4j;

/**
 * 平台通用应答
 */
@Slf4j
@SuppressWarnings({"FieldCanBeLocal"})
public class JT808_Message_Handler_0x8001 implements IJT808MessageHandler<JT808Message, JT808Message> {
    public static final JT808MessageId MESSAGE_ID = JT808MessageId.JT808_Message_0x8001;

    private final ISpecificationContext ctx;

    public JT808_Message_Handler_0x8001(ISpecificationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public JT808MessageId getMessageId() {
        return MESSAGE_ID;
    }

    @Override
    public void handle(JT808MessageHandlerContext ctx, JT808Message message) {
        String json = Jackson.toJsonPrettyString(message);
        log.info("从服务器接收到消息, 消息ID[{}], 消息名称[{}], 协议版本[{}], 连接ID[{}]{}{}",
                message.getHeader().getMessageId().getName(),
                message.getHeader().getMessageId().getDescription(),
                message.getHeader().getProtocolVersion().getName(),
                ctx.getConnection().getConnectionId(),
                System.lineSeparator(), json);

        // 无需处理, 只打日志
    }
}