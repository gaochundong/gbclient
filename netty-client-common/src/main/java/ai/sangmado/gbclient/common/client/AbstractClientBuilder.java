package ai.sangmado.gbclient.common.client;

import ai.sangmado.gbclient.common.channel.ConnectionFactory;
import ai.sangmado.gbclient.common.channel.UnpooledConnectionFactory;
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

    protected PipelineConfigurator<I, O> pipelineConfigurator;
    protected ClientConfig clientConfig;

    protected ConnectionFactory<I, O> connectionFactory;
    protected ConnectionHandler<I, O> connectionHandler;

    protected EventLoopGroup eventLoopGroup;
    protected Class<? extends Channel> clientChannelClass;

    protected AbstractClientBuilder(
            ServerInfo serverInfo, ConnectionHandler<I, O> connectionHandler) {
        this(serverInfo, connectionHandler, new Bootstrap());
    }

    protected AbstractClientBuilder(
            ServerInfo serverInfo, ConnectionHandler<I, O> connectionHandler, Bootstrap bootstrap) {
        this(serverInfo, connectionHandler, bootstrap, new UnpooledConnectionFactory<>());
    }

    protected AbstractClientBuilder(
            ServerInfo serverInfo, ConnectionHandler<I, O> connectionHandler, Bootstrap bootstrap, ConnectionFactory<I, O> connectionFactory) {
        this.serverInfo = serverInfo;
        this.connectionHandler = connectionHandler;
        this.clientBootstrap = bootstrap;
        this.connectionFactory = connectionFactory;

        this.clientConfig = new ClientConfig();
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

    public B pipelineConfigurator(PipelineConfigurator<I, O> pipelineConfigurator) {
        this.pipelineConfigurator = pipelineConfigurator;
        return returnBuilder();
    }

    public B appendPipelineConfigurator(PipelineConfigurator<I, O> additionalConfigurator) {
        return pipelineConfigurator(new PipelineConfiguratorComposite<>(pipelineConfigurator, additionalConfigurator));
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

    public PipelineConfigurator<I, O> getPipelineConfigurator() {
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
