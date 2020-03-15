package ai.sangmado.gbclient.common.client;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbclient.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbclient.common.pipeline.PipelineConfiguratorComposite;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 业务客户端抽象类
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
@SuppressWarnings({"UnusedReturnValue", "unchecked"})
public abstract class AbstractClient<I, O> {

    protected final ServerInfo serverInfo;
    protected final Bootstrap clientBootstrap;

    protected final PipelineConfigurator<O, I> pipelineConfigurator;
    protected final ClientConfig clientConfig;

    protected final ClientChannelFactory<O, I> channelFactory;
    protected final ClientConnectionFactory<O, I> connectionFactory;

    private final AtomicBoolean isShutdown = new AtomicBoolean();
    private Connection<O, I> connection = null;

    protected AbstractClient(
            ServerInfo serverInfo, Bootstrap clientBootstrap,
            PipelineConfigurator<O, I> pipelineConfigurator,
            ClientConfig clientConfig,
            ClientChannelFactory<O, I> channelFactory,
            ClientConnectionFactory<O, I> connectionFactory) {
        if (null == clientBootstrap) throw new NullPointerException("Client bootstrap can not be null.");
        if (null == serverInfo) throw new NullPointerException("Server info can not be null.");
        if (null == clientConfig) throw new NullPointerException("Client config can not be null.");
        if (null == channelFactory) throw new NullPointerException("Channel factory can not be null.");
        if (null == connectionFactory) throw new NullPointerException("Connection factory can not be null.");

        this.serverInfo = serverInfo;
        this.clientBootstrap = clientBootstrap;
        this.pipelineConfigurator = pipelineConfigurator;
        this.clientConfig = clientConfig;
        this.channelFactory = channelFactory;
        this.connectionFactory = connectionFactory;

        this.clientBootstrap.handler(newChannelInitializer(pipelineConfigurator, clientConfig));
    }

    protected ChannelInitializer<Channel> newChannelInitializer(
            PipelineConfigurator<O, I> pipelineConfigurator, ClientConfig clientConfig) {
        PipelineConfigurator<O, I> configurator = adaptPipelineConfigurator(pipelineConfigurator, clientConfig);
        return new ChannelInitializer<Channel>() {
            @Override
            public void initChannel(Channel ch) throws Exception {
                configurator.configureNewPipeline(ch.pipeline());
            }
        };
    }

    protected PipelineConfigurator<O, I> adaptPipelineConfigurator(
            PipelineConfigurator<O, I> pipelineConfigurator, ClientConfig clientConfig) {
        PipelineConfigurator<O, I> clientRequiredConfigurator;

        if (clientConfig.isReadTimeoutSet()) {
            clientRequiredConfigurator = new PipelineConfiguratorComposite<>(new ClientRequiredConfigurator<O, I>());
        } else {
            clientRequiredConfigurator = new ClientRequiredConfigurator<>();
        }

        if (pipelineConfigurator == null) {
            pipelineConfigurator = clientRequiredConfigurator;
        } else {
            pipelineConfigurator = new PipelineConfiguratorComposite<>(pipelineConfigurator, clientRequiredConfigurator);
        }
        return pipelineConfigurator;
    }

    public Connection<O, I> connect() throws Exception {
        if (isShutdown.get()) {
            throw new IllegalStateException("Client is already shutdown.");
        }

        ChannelFuture f = channelFactory.connect(serverInfo);
        Channel channel = f.syncUninterruptibly().channel();
        if (!f.isSuccess()) {
            throw (Exception) f.cause();
        }
        this.connection = connectionFactory.newConnection(channel);
        ChannelPipeline pipeline = channel.pipeline();
        ChannelHandler lifecycleHandler = pipeline.get(ClientRequiredConfigurator.CONNECTION_LIFECYCLE_HANDLER_NAME);
        ((ConnectionLifecycleHandler<O, I>) lifecycleHandler).setConnection(this.connection);

        return this.connection;
    }

    public void shutdown() {
        if (!isShutdown.compareAndSet(false, true)) {
            return;
        }

        if (this.connection != null) {
            this.connection.close();
        }
    }
}
