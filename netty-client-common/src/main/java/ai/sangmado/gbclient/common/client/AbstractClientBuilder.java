package ai.sangmado.gbclient.common.client;

import ai.sangmado.gbclient.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbclient.common.pipeline.PipelineConfiguratorComposite;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 业务客户端构造器抽象类
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 * @param <B> 业务客户端构造器
 * @param <C> 业务客户端
 */
@SuppressWarnings({"rawtypes", "UnusedReturnValue", "unused"})
public abstract class AbstractClientBuilder<
        I,
        O,
        B extends AbstractClientBuilder,
        C extends AbstractClient<I, O>> {

    protected final ServerInfo serverInfo;
    protected final Bootstrap clientBootstrap;
    protected ClientConnectionFactory<O, I> connectionFactory;
    protected ClientChannelFactory<O, I> channelFactory;

    protected ClientConfig clientConfig;
    protected EventLoopGroup eventLoopGroup;
    protected PipelineConfigurator<O, I> pipelineConfigurator;
    protected Class<? extends Channel> clientChannelClass;

    protected AbstractClientBuilder(ServerInfo serverInfo) {
        this(serverInfo, new Bootstrap());
    }

    protected AbstractClientBuilder(ServerInfo serverInfo, Bootstrap bootstrap) {
        this(serverInfo, bootstrap,
                new UnpooledClientConnectionFactory<>(),
                new ClientChannelFactoryImpl<>(bootstrap));
    }

    protected AbstractClientBuilder(
            ServerInfo serverInfo, Bootstrap bootstrap,
            ClientConnectionFactory<O, I> connectionFactory,
            ClientChannelFactory<O, I> channelFactory) {
        this.clientBootstrap = bootstrap;
        this.serverInfo = serverInfo;
        this.clientConfig = new ClientConfig();
        this.connectionFactory = connectionFactory;
        this.channelFactory = channelFactory;
        defaultChannelOptions();
    }

    protected EventLoopGroup defaultEventLoop(Class<? extends Channel> socketChannel) {
        return new NioEventLoopGroup();
    }

    protected Class<? extends SocketChannel> defaultClientChannelClass() {
        return NioSocketChannel.class;
    }

    protected abstract C createClient();

    public B config(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        return returnBuilder();
    }

    public B defaultChannelOptions() {
        return channelOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    }

    public B defaultTcpOptions() {
        defaultChannelOptions();
        return channelOption(ChannelOption.TCP_NODELAY, true);
    }

    public <T> B channelOption(ChannelOption<T> option, T value) {
        clientBootstrap.option(option, value);
        return returnBuilder();
    }

    public B channel(Class<? extends Channel> socketChannel) {
        this.clientChannelClass = socketChannel;
        return returnBuilder();
    }

    public B eventLoop(EventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
        return returnBuilder();
    }

    public B pipelineConfigurator(PipelineConfigurator<O, I> pipelineConfigurator) {
        this.pipelineConfigurator = pipelineConfigurator;
        return returnBuilder();
    }

    public B appendPipelineConfigurator(PipelineConfigurator<O, I> additionalConfigurator) {
        return pipelineConfigurator(new PipelineConfiguratorComposite<>(pipelineConfigurator, additionalConfigurator));
    }

    public B withChannelFactory(ClientChannelFactory<O, I> factory) {
        this.channelFactory = factory;
        return returnBuilder();
    }

    @SuppressWarnings("unchecked")
    protected B returnBuilder() {
        return (B) this;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public Bootstrap getClientBootstrap() {
        return clientBootstrap;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public PipelineConfigurator<O, I> getPipelineConfigurator() {
        return pipelineConfigurator;
    }

    public C build() {
        if (clientChannelClass == null) {
            clientChannelClass = defaultClientChannelClass();
            if (eventLoopGroup == null) {
                eventLoopGroup = defaultEventLoop(clientChannelClass);
            }
        }

        if (eventLoopGroup == null) {
            if (defaultClientChannelClass() == clientChannelClass) {
                eventLoopGroup = defaultEventLoop(clientChannelClass);
            } else {
                throw new IllegalStateException("Specified a channel class but not the event loop group.");
            }
        }

        clientBootstrap.channel(clientChannelClass).group(eventLoopGroup);

        return createClient();
    }
}
