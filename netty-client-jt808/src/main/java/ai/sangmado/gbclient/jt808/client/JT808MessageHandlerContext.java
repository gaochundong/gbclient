package ai.sangmado.gbclient.jt808.client;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;

/**
 * 消息处理上下文
 */
public interface JT808MessageHandlerContext {

    /**
     * 获取通道连接
     *
     * @return 通道连接
     */
    Connection<JT808Message, JT808Message> getConnection();
}
