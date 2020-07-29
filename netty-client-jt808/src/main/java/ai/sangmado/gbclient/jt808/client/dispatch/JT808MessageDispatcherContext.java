package ai.sangmado.gbclient.jt808.client.dispatch;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbclient.jt808.client.JT808MessageHandlerContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;

/**
 * 消息处理上下文
 */
public class JT808MessageDispatcherContext implements JT808MessageHandlerContext {
    private final Connection<JT808Message, JT808Message> connection;

    public JT808MessageDispatcherContext(Connection<JT808Message, JT808Message> connection) {
        this.connection = connection;
    }

    @Override
    public Connection<JT808Message, JT808Message> getConnection() {
        return connection;
    }
}
