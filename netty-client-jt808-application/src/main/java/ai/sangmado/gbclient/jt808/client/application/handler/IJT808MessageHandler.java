package ai.sangmado.gbclient.jt808.client.application.handler;

import ai.sangmado.gbclient.common.channel.Connection;
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
     * @param connection 服务器连接
     * @param message    消息
     */
    void handle(Connection<I, O> connection, I message);
}