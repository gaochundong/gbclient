package ai.sangmado.gbclient.common.client;

import ai.sangmado.gbclient.common.channel.ConnectionFactory;
import ai.sangmado.gbclient.common.pipeline.PipelineConfigurator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * 业务客户端管道配置
 */
public class ClientRequiredConfigurator<I, O> implements PipelineConfigurator<I, O> {
    public static final String CONNECTION_LIFECYCLE_HANDLER_NAME = "连接生命周期管理器";

    private final ConnectionHandler<I, O> connectionHandler;
    private final ConnectionFactory<I, O> connectionFactory;
    private final EventExecutorGroup connectionHandlingExecutor;

    public ClientRequiredConfigurator(
            ConnectionHandler<I, O> connectionHandler,
            ConnectionFactory<I, O> connectionFactory) {
        this(connectionHandler, connectionFactory, null);
    }

    public ClientRequiredConfigurator(
            ConnectionHandler<I, O> connectionHandler,
            ConnectionFactory<I, O> connectionFactory,
            EventExecutorGroup connectionHandlingExecutor) {
        this.connectionHandler = connectionHandler;
        this.connectionFactory = connectionFactory;
        this.connectionHandlingExecutor = connectionHandlingExecutor;
    }

    @Override
    public void configureNewPipeline(ChannelPipeline pipeline) {
        ChannelHandler lifecycleHandler = new ConnectionLifecycleHandler<>(connectionHandler, connectionFactory);
        pipeline.addLast(connectionHandlingExecutor, CONNECTION_LIFECYCLE_HANDLER_NAME, lifecycleHandler);
    }
}