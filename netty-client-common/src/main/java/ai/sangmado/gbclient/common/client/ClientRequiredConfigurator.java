package ai.sangmado.gbclient.common.client;

import ai.sangmado.gbclient.common.pipeline.PipelineConfigurator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * 业务客户端管道配置
 */
public class ClientRequiredConfigurator<I, O> implements PipelineConfigurator<I, O> {
    public static final String CONNECTION_LIFECYCLE_HANDLER_NAME = "CONNECTION_LIFECYCLE_HANDLER";

    private final EventExecutorGroup connectionHandlingExecutor;

    public ClientRequiredConfigurator() {
        this(null);
    }

    public ClientRequiredConfigurator(EventExecutorGroup connectionHandlingExecutor) {
        this.connectionHandlingExecutor = connectionHandlingExecutor;
    }

    @Override
    public void configureNewPipeline(ChannelPipeline pipeline) {
        ChannelHandler lifecycleHandler = new ConnectionLifecycleHandler<I, O>();
        pipeline.addLast(connectionHandlingExecutor, CONNECTION_LIFECYCLE_HANDLER_NAME, lifecycleHandler);
    }
}