package ai.sangmado.gbclient.jt808.client.dispatch;

import ai.sangmado.gbclient.jt808.client.JT808MessageHandlerContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 接收到JT808消息通知事件
 */
@Getter
@NoArgsConstructor
public class JT808MessageReceivedEvent {

    public JT808MessageReceivedEvent(JT808MessageHandlerContext context, JT808Message message) {
        this.context = context;
        this.message = message;
    }

    private JT808MessageHandlerContext context;

    private JT808Message message;
}