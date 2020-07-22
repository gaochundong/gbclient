package ai.sangmado.gbclient.common.client;

import ai.sangmado.gbclient.common.channel.ConnectionFactory;
import ai.sangmado.gbclient.common.pipeline.PipelineConfigurator;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * 面向连接的客户端
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
@SuppressWarnings({"UnusedReturnValue"})
public abstract class ConnectionBasedClient<I, O> extends AbstractClient<I, O> {

    protected PipelineConfigurator<I, O> pipelineConfigurator;

    public ConnectionBasedClient(
            ServerInfo serverInfo,
            Bootstrap clientBootstrap,
            ClientConfig clientConfig,
            PipelineConfigurator<I, O> pipelineConfigurator,
            ConnectionFactory<I, O> connectionFactory,
            ConnectionHandler<I, O> connectionHandler,
            EventExecutorGroup connHandlingExecutor) {
        super(serverInfo, clientBootstrap, clientConfig, connectionFactory, connectionHandler, connHandlingExecutor);
        this.pipelineConfigurator = pipelineConfigurator;

        // 配置通道处理器用于响应连接请求
        this.clientBootstrap.handler(newChannelInitializer(pipelineConfigurator, connectionHandler, connHandlingExecutor, clientConfig));

        // 配置IO参数
        if (clientConfig.isConnectTimeoutSet()) {
            this.clientBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientConfig.getConnectTimeoutInMillis());
        }
    }
}
