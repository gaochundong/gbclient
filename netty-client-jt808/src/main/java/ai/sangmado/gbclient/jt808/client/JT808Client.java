package ai.sangmado.gbclient.jt808.client;

import ai.sangmado.gbclient.common.channel.ConnectionFactory;
import ai.sangmado.gbclient.common.client.ClientConfig;
import ai.sangmado.gbclient.common.client.ConnectionBasedClient;
import ai.sangmado.gbclient.common.client.ConnectionHandler;
import ai.sangmado.gbclient.common.client.ServerInfo;
import ai.sangmado.gbclient.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * JT808 业务客户端
 */
@Slf4j
@SuppressWarnings({"UnusedReturnValue", "unused", "FieldCanBeLocal"})
public class JT808Client<I extends JT808MessagePacket, O extends JT808MessagePacket> extends ConnectionBasedClient<I, O> {

    public JT808Client(
            String host, int port,
            Bootstrap clientBootstrap,
            ClientConfig clientConfig,
            PipelineConfigurator<I, O> pipelineConfigurator,
            ConnectionFactory<I, O> connectionFactory,
            ConnectionHandler<I, O> connectionHandler,
            EventExecutorGroup connHandlingExecutor) {
        this(new ServerInfo(host, port),
                clientBootstrap, clientConfig, pipelineConfigurator,
                connectionFactory, connectionHandler, connHandlingExecutor);
    }

    protected JT808Client(
            ServerInfo serverInfo,
            Bootstrap clientBootstrap,
            ClientConfig clientConfig,
            PipelineConfigurator<I, O> pipelineConfigurator,
            ConnectionFactory<I, O> connectionFactory,
            ConnectionHandler<I, O> connectionHandler,
            EventExecutorGroup connHandlingExecutor) {
        super(serverInfo,
                clientBootstrap, clientConfig, pipelineConfigurator,
                connectionFactory, connectionHandler, connHandlingExecutor);
    }
}
