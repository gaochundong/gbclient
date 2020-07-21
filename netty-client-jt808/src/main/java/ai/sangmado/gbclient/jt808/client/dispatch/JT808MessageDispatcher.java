package ai.sangmado.gbclient.jt808.client.dispatch;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * JT808 消息分发器
 */
@Slf4j
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class JT808MessageDispatcher<I extends JT808MessagePacket, O extends JT808MessagePacket>
        extends SubmissionPublisher<JT808MessageReceivedEvent<I, O>> {

    public JT808MessageDispatcher() {
        super();
    }

    public void dispatch(Connection<I, O> connection, I message) {
        // 通过 Flow 分发消息至业务域
        submit(new JT808MessageReceivedEvent<>(connection, message));
    }

    public JT808MessageDispatcher<I, O> bindSubscriber(Flow.Subscriber<JT808MessageReceivedEvent<I, O>> subscriber) {
        // 业务域订阅消息分发
        this.subscribe(subscriber);
        return this;
    }
}