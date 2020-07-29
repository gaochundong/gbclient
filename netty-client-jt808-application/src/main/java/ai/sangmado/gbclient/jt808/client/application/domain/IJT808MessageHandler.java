package ai.sangmado.gbclient.jt808.client.application.domain;

import ai.sangmado.gbclient.jt808.client.JT808MessageHandlerContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;

/**
 * JT808 消息业务处理器
 */
public interface IJT808MessageHandler<I, O> {

    /**
     * 处理的消息ID
     *
     * @return 消息ID
     */
    JT808MessageId getMessageId();

    /**
     * 处理消息
     *
     * @param ctx     消息处理上下文
     * @param message 消息
     */
    void handle(JT808MessageHandlerContext ctx, I message);
}