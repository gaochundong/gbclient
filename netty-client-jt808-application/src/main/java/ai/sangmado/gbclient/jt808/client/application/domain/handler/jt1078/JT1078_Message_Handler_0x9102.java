package ai.sangmado.gbclient.jt808.client.application.domain.handler.jt1078;

import ai.sangmado.gbclient.jt808.client.JT808MessageHandlerContext;
import ai.sangmado.gbclient.jt808.client.application.domain.IJT808MessageHandler;
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
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessageAssembler;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808MessageContent;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x0001;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 平台下发音视频实时传输控制
 */
@Slf4j
@SuppressWarnings({"SameParameterValue"})
public class JT1078_Message_Handler_0x9102 implements IJT808MessageHandler<JT808Message, JT808Message> {
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
    public void handle(JT808MessageHandlerContext ctx, JT808Message message) {
        String json = Jackson.toJsonPrettyString(message);
        log.info("从服务器接收到消息, 消息ID[{}], 消息名称[{}], 协议版本[{}], 连接ID[{}]{}{}",
                message.getHeader().getMessageId().getName(),
                message.getHeader().getMessageId().getDescription(),
                message.getHeader().getProtocolVersion().getName(),
                ctx.getConnection().getConnectionId(),
                System.lineSeparator(), json);

        // 根据协议版本判断消息头和消息体类型
        JT808ProtocolVersion protocolVersion = message.getProtocolVersion();
        if (JT808ProtocolVersion.V2011.equals(protocolVersion)) {
            JT808MessageHeader2011 header = (JT808MessageHeader2011) message.getHeader();
            JT1078_Message_Content_0x9102 content = (JT1078_Message_Content_0x9102) message.getContent();
            log.info("平台下发音视频实时传输控制, 协议版本[{}], 通道号[{}], 控制指令[{}]",
                    header.getProtocolVersion(), content.getLogicalChannelNumber(), content.getChannelControlCommand());
        } else if (JT808ProtocolVersion.V2013.equals(protocolVersion)) {
            JT808MessageHeader2013 header = (JT808MessageHeader2013) message.getHeader();
            JT1078_Message_Content_0x9102 content = (JT1078_Message_Content_0x9102) message.getContent();
            log.info("平台下发音视频实时传输控制, 协议版本[{}], 通道号[{}], 控制指令[{}]",
                    header.getProtocolVersion(), content.getLogicalChannelNumber(), content.getChannelControlCommand());
        } else if (JT808ProtocolVersion.V2019.equals(protocolVersion)) {
            JT808MessageHeader2019 header = (JT808MessageHeader2019) message.getHeader();
            JT1078_Message_Content_0x9102 content = (JT1078_Message_Content_0x9102) message.getContent();
            log.info("平台下发音视频实时传输控制, 协议版本[{}], 版本号[{}], 通道号[{}], 控制指令[{}]",
                    header.getProtocolVersion(), header.getVersionNumber(), content.getLogicalChannelNumber(), content.getChannelControlCommand());
        } else {
            throw new UnsupportedJT808ProtocolVersionException(protocolVersion);
        }

        JT808Message response = create_JT808_Message_0x0001_packet(
                buildCoordinatedContext(protocolVersion),
                message.getHeader().getPhoneNumber(),
                message.getMessageId(),
                message.getHeader().getSerialNumber(),
                JT808DeviceCommonReplyResult.Success);
        ctx.getConnection().writeAndFlush(response);
        log.info("平台下发音视频实时传输控制, 协议版本[{}], 回复成功[{}]", protocolVersion, response.getMessageId());
    }

    // 创建新的协议上下文 - 保持与请求协议版本一致
    private JT808ProtocolSpecificationContext buildCoordinatedContext(JT808ProtocolVersion protocolVersion) {
        JT808ProtocolSpecificationContext newContext = new JT808ProtocolSpecificationContext();
        newContext.setProtocolVersion(protocolVersion);
        newContext.setByteOrder(this.ctx.getByteOrder());
        newContext.setCharset(this.ctx.getCharset());
        newContext.setBufferPool(this.ctx.getBufferPool());
        return newContext;
    }

    // 终端通用应答
    private static JT808Message create_JT808_Message_0x0001_packet(
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

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }
}