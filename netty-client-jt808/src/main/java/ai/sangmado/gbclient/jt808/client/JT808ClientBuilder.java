package ai.sangmado.gbclient.jt808.client;

import ai.sangmado.gbclient.common.client.AbstractClientBuilder;
import ai.sangmado.gbclient.common.client.ConnectionHandler;
import ai.sangmado.gbclient.common.client.ServerInfo;
import ai.sangmado.gbclient.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import io.netty.bootstrap.Bootstrap;
import lombok.extern.slf4j.Slf4j;

/**
 * JT808 业务客户端构造器
 */
@Slf4j
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class JT808ClientBuilder<I extends JT808MessagePacket, O extends JT808MessagePacket> extends AbstractClientBuilder<I, O, JT808ClientBuilder<I, O>, JT808Client<I, O>> {

    public JT808ClientBuilder(String host, int port, PipelineConfigurator<I, O> pipelineConfigurator, ConnectionHandler<I, O> connectionHandler) {
        super(new ServerInfo(host, port), connectionHandler);
        this.pipelineConfigurator(pipelineConfigurator);
    }

    public JT808ClientBuilder(String host, int port, PipelineConfigurator<I, O> pipelineConfigurator, ConnectionHandler<I, O> connectionHandler, Bootstrap bootstrap) {
        super(new ServerInfo(host, port), connectionHandler, bootstrap);
        this.pipelineConfigurator(pipelineConfigurator);
    }

    @Override
    protected JT808Client<I, O> createClient() {
        return new JT808Client<>(
                serverInfo,
                clientBootstrap,
                clientConfig,
                pipelineConfigurator,
                connectionFactory,
                connectionHandler,
                eventLoopGroup);
    }
}
