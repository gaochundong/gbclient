package ai.sangmado.gbclient.jt808.client;

import ai.sangmado.gbclient.common.channel.ConnectionFactory;
import ai.sangmado.gbclient.common.client.ClientConfig;
import ai.sangmado.gbclient.common.client.ConnectionBasedClient;
import ai.sangmado.gbclient.common.client.ConnectionHandler;
import ai.sangmado.gbclient.common.client.ServerInfo;
import ai.sangmado.gbclient.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * JT808 业务客户端
 */
@Slf4j
@SuppressWarnings({"UnusedReturnValue", "unused", "FieldCanBeLocal"})
public class JT808Client extends ConnectionBasedClient<JT808Message, JT808Message> {

    public JT808Client(
            String host, int port,
            Bootstrap clientBootstrap,
            ClientConfig clientConfig,
            PipelineConfigurator<JT808Message, JT808Message> pipelineConfigurator,
            ConnectionFactory<JT808Message, JT808Message> connectionFactory,
            ConnectionHandler<JT808Message, JT808Message> connectionHandler,
            EventExecutorGroup connHandlingExecutor) {
        this(new ServerInfo(host, port),
                clientBootstrap, clientConfig, pipelineConfigurator,
                connectionFactory, connectionHandler, connHandlingExecutor);
    }

    protected JT808Client(
            ServerInfo serverInfo,
            Bootstrap clientBootstrap,
            ClientConfig clientConfig,
            PipelineConfigurator<JT808Message, JT808Message> pipelineConfigurator,
            ConnectionFactory<JT808Message, JT808Message> connectionFactory,
            ConnectionHandler<JT808Message, JT808Message> connectionHandler,
            EventExecutorGroup connHandlingExecutor) {
        super(serverInfo,
                clientBootstrap, clientConfig, pipelineConfigurator,
                connectionFactory, connectionHandler, connHandlingExecutor);
    }
}
