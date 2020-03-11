package ai.sangmado.gbclient.jt808.client;

import ai.sangmado.gbclient.common.client.*;
import ai.sangmado.gbclient.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import io.netty.bootstrap.Bootstrap;

/**
 * JT808 业务客户端
 *
 * @param <I> 读取 JT808 业务消息
 * @param <O> 写入 JT808 业务消息
 */
@SuppressWarnings({"UnusedReturnValue", "unused", "FieldCanBeLocal"})
public class JT808Client<I, O> extends AbstractClient<I, O> implements IJT808Client<I, O> {
    private final ISpecificationContext ctx;

    public JT808Client(
            ISpecificationContext ctx, String host, int port, Bootstrap clientBootstrap) {
        this(ctx, host, port, clientBootstrap, null, null, null, null);
    }

    public JT808Client(
            ISpecificationContext ctx, String host, int port, Bootstrap clientBootstrap,
            PipelineConfigurator<O, I> pipelineConfigurator,
            ClientConfig clientConfig,
            ClientChannelFactory<O, I> channelFactory,
            ClientConnectionFactory<O, I> connectionFactory) {
        this(ctx, new ServerInfo(host, port), clientBootstrap, pipelineConfigurator, clientConfig, channelFactory, connectionFactory);
    }

    protected JT808Client(
            ISpecificationContext ctx, ServerInfo serverInfo, Bootstrap clientBootstrap,
            PipelineConfigurator<O, I> pipelineConfigurator,
            ClientConfig clientConfig,
            ClientChannelFactory<O, I> channelFactory,
            ClientConnectionFactory<O, I> connectionFactory) {
        super(serverInfo, clientBootstrap, pipelineConfigurator, clientConfig, channelFactory, connectionFactory);
        this.ctx = ctx;
    }
}
