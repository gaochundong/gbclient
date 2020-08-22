package ai.sangmado.gbclient.jt808.client;

import ai.sangmado.gbclient.common.client.AbstractClientBuilder;
import ai.sangmado.gbclient.common.client.ConnectionHandler;
import ai.sangmado.gbclient.common.client.ServerInfo;
import ai.sangmado.gbclient.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import lombok.extern.slf4j.Slf4j;

/**
 * JT808 业务客户端构造器
 */
@Slf4j
public class JT808ClientBuilder extends AbstractClientBuilder<JT808Message, JT808Message, JT808ClientBuilder, JT808Client> {

    public JT808ClientBuilder(String host, int port, ConnectionHandler<JT808Message, JT808Message> connectionHandler, PipelineConfigurator<JT808Message, JT808Message> pipelineConfigurator) {
        super(new ServerInfo(host, port), connectionHandler);
        this.pipelineConfigurator(pipelineConfigurator);
    }

    @Override
    protected JT808Client createClient() {
        return new JT808Client(
                serverInfo,
                clientBootstrap,
                clientConfig,
                pipelineConfigurator,
                connectionFactory,
                connectionHandler,
                eventLoopGroup);
    }
}
