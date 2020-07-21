package ai.sangmado.gbclient.jt808.client.application.handler.jt1078;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbclient.jt808.client.application.handler.IJT808MessageHandler;
import ai.sangmado.gbclient.jt808.client.utils.GlobalSerialNumberIssuer;
import ai.sangmado.gbclient.jt808.client.utils.Jackson;
import ai.sangmado.gbprotocol.jt1078.protocol.enums.JT1078MessageId;
import ai.sangmado.gbprotocol.jt1078.protocol.message.content.JT1078_Message_Content_0x9102;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808DeviceCommonReplyResult;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808ProtocolVersion;
import ai.sangmado.gbprotocol.jt808.protocol.exceptions.UnsupportedJT808ProtocolVersionException;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacketBuilder;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808MessageContent;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x0001;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeader;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeaderFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 平台下发音视频实时传输控制
 */
@Slf4j
@SuppressWarnings({"unchecked", "SameParameterValue"})
public class JT1078_Message_Handler_0x9102<I extends JT808MessagePacket, O extends JT808MessagePacket>
        implements IJT808MessageHandler<I, O> {
    public static final JT1078MessageId MESSAGE_ID = JT1078MessageId.JT1078_Message_0x9102;

    private final ISpecificationContext ctx;

    public JT1078_Message_Handler_0x9102(ISpecificationContext ctx) {
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

        JT808ProtocolVersion protocolVersion = message.getProtocolVersion();
        JT1078_Message_Content_0x9102 content = (JT1078_Message_Content_0x9102) message.getContent();

        if (JT808ProtocolVersion.V2011.equals(protocolVersion)) {
            log.info("平台下发音视频实时传输控制, 协议版本[{}], 通道号[{}], 控制指令[{}]", protocolVersion, content.getLogicalChannelNumber(), content.getChannelControlCommand());
        } else if (JT808ProtocolVersion.V2013.equals(protocolVersion)) {
            log.info("平台下发音视频实时传输控制, 协议版本[{}], 通道号[{}], 控制指令[{}]", protocolVersion, content.getLogicalChannelNumber(), content.getChannelControlCommand());
        } else if (JT808ProtocolVersion.V2019.equals(protocolVersion)) {
            log.info("平台下发音视频实时传输控制, 协议版本[{}], 通道号[{}], 控制指令[{}]", protocolVersion, content.getLogicalChannelNumber(), content.getChannelControlCommand());
        } else {
            throw new UnsupportedJT808ProtocolVersionException(protocolVersion);
        }

        JT808MessagePacket response = create_JT808_Message_0x0001_packet(
                buildComplianceContext(protocolVersion),
                message.getHeader().getPhoneNumber(),
                message.getMessageId(),
                message.getHeader().getSerialNumber(),
                JT808DeviceCommonReplyResult.Success);
        connection.writeAndFlush((O) response);
        log.info("平台下发音视频实时传输控制, 协议版本[{}], 回复成功[{}]", protocolVersion, response.getMessageId());
    }

    // 创建新的协议上下文 - 保持与请求协议版本一致
    private JT808ProtocolSpecificationContext buildComplianceContext(JT808ProtocolVersion protocolVersion) {
        JT808ProtocolSpecificationContext newContext = new JT808ProtocolSpecificationContext();
        newContext.setProtocolVersion(protocolVersion);
        newContext.setByteOrder(this.ctx.getByteOrder());
        newContext.setCharset(this.ctx.getCharset());
        newContext.setBufferPool(this.ctx.getBufferPool());
        return newContext;
    }

    // 终端通用应答
    private static JT808MessagePacket create_JT808_Message_0x0001_packet(
            ISpecificationContext ctx,
            String phoneNumber,
            JT808MessageId ackId,
            int ackSerialNumber,
            JT808DeviceCommonReplyResult result) {
        JT808MessageId messageId = JT808MessageId.JT808_Message_0x0001;
        int serialNumber = GlobalSerialNumberIssuer.next(100);

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT808_Message_Content_0x0001.builder()
                .ackSerialNumber(ackSerialNumber)
                .ackId(ackId)
                .result(result)
                .build();

        List<JT808MessagePacket> packets = JT808MessagePacketBuilder.buildPackets(ctx, header, content);
        return packets.get(0);
    }
}