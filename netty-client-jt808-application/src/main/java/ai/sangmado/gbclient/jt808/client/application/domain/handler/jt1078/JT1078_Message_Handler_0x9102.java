package ai.sangmado.gbclient.jt808.client.application.domain.handler.jt1078;

import ai.sangmado.gbclient.jt808.client.JT808MessageHandlerContext;
import ai.sangmado.gbclient.jt808.client.application.domain.handler.AbstractJT808MessageHandler;
import ai.sangmado.gbclient.jt808.client.utils.GlobalSerialNumberIssuer;
import ai.sangmado.gbprotocol.jt1078.protocol.enums.JT1078MessageId;
import ai.sangmado.gbprotocol.jt1078.protocol.message.content.JT1078_Message_Content_0x9102;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.IVersionedSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolVersionedSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808DeviceCommonReplyResult;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
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
public class JT1078_Message_Handler_0x9102 extends AbstractJT808MessageHandler {
    public static final JT1078MessageId MESSAGE_ID = JT1078MessageId.JT1078_Message_0x9102;

    public JT1078_Message_Handler_0x9102(ISpecificationContext ctx) {
        super(ctx);
    }

    @Override
    public JT808MessageId getMessageId() {
        return MESSAGE_ID;
    }

    @Override
    protected void handleV2011Message(JT808MessageHandlerContext ctx, JT808MessageHeader2011 header, JT808MessageContent content) {
        JT1078_Message_Content_0x9102 instance = (JT1078_Message_Content_0x9102) content;
        log.info("平台下发音视频实时传输控制, 协议版本[{}], 通道号[{}], 控制指令[{}]",
                header.getProtocolVersion(), instance.getLogicalChannelNumber(), instance.getChannelControlCommand());

        JT808Message response = create_JT808_Message_0x0001_packet(
                JT808ProtocolVersionedSpecificationContext.buildFrom(header.getProtocolVersion(), this.getContext()),
                header.getPhoneNumber(),
                header.getMessageId(),
                header.getSerialNumber(),
                JT808DeviceCommonReplyResult.Success);
        ctx.getConnection().writeAndFlush(response);
        log.info("平台下发音视频实时传输控制, 协议版本[{}], 回复成功[{}]", header.getProtocolVersion(), response.getMessageId());
    }

    @Override
    protected void handleV2013Message(JT808MessageHandlerContext ctx, JT808MessageHeader2013 header, JT808MessageContent content) {
        JT1078_Message_Content_0x9102 instance = (JT1078_Message_Content_0x9102) content;
        log.info("平台下发音视频实时传输控制, 协议版本[{}], 通道号[{}], 控制指令[{}]",
                header.getProtocolVersion(), instance.getLogicalChannelNumber(), instance.getChannelControlCommand());

        JT808Message response = create_JT808_Message_0x0001_packet(
                JT808ProtocolVersionedSpecificationContext.buildFrom(header.getProtocolVersion(), this.getContext()),
                header.getPhoneNumber(),
                header.getMessageId(),
                header.getSerialNumber(),
                JT808DeviceCommonReplyResult.Success);
        ctx.getConnection().writeAndFlush(response);
        log.info("平台下发音视频实时传输控制, 协议版本[{}], 回复成功[{}]", header.getProtocolVersion(), response.getMessageId());
    }

    @Override
    protected void handleV2019Message(JT808MessageHandlerContext ctx, JT808MessageHeader2019 header, JT808MessageContent content) {
        JT1078_Message_Content_0x9102 instance = (JT1078_Message_Content_0x9102) content;
        log.info("平台下发音视频实时传输控制, 协议版本[{}], 版本号[{}], 通道号[{}], 控制指令[{}]",
                header.getProtocolVersion(), header.getVersionNumber(), instance.getLogicalChannelNumber(), instance.getChannelControlCommand());

        JT808Message response = create_JT808_Message_0x0001_packet(
                JT808ProtocolVersionedSpecificationContext.buildFrom(header.getProtocolVersion(), this.getContext()),
                header.getPhoneNumber(),
                header.getMessageId(),
                header.getSerialNumber(),
                JT808DeviceCommonReplyResult.Success);
        ctx.getConnection().writeAndFlush(response);
        log.info("平台下发音视频实时传输控制, 协议版本[{}], 回复成功[{}]", header.getProtocolVersion(), response.getMessageId());
    }

    // 终端通用应答
    private static JT808Message create_JT808_Message_0x0001_packet(
            IVersionedSpecificationContext ctx,
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