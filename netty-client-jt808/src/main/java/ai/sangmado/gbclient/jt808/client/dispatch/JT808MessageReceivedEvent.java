package ai.sangmado.gbclient.jt808.client.dispatch;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 接收到JT808消息通知事件
 */
@Getter
@Setter
@NoArgsConstructor
public class JT808MessageReceivedEvent<I extends JT808MessagePacket, O extends JT808MessagePacket> {

    public JT808MessageReceivedEvent(Connection<I, O> connection, I message) {
        this.setConnection(connection);
        this.setMessage(message);
    }

    private Connection<I, O> connection;

    private I message;
}