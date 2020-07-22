package ai.sangmado.gbclient.common.client;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbclient.common.channel.ConnectionFactory;
import ai.sangmado.gbclient.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbclient.common.pipeline.PipelineConfiguratorComposite;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ConnectTimeoutException;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.CancellationException;

/**
 * 业务客户端抽象类
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
@Slf4j
@SuppressWarnings({"UnusedReturnValue"})
public abstract class AbstractClient<I, O> {

    protected final ServerInfo serverInfo;
    protected final Bootstrap clientBootstrap;
    protected final ClientConfig clientConfig;

    protected final ConnectionFactory<I, O> connectionFactory;
    protected final ConnectionHandler<I, O> connectionHandler;
    protected final EventExecutorGroup connHandlingExecutor;

    protected AbstractClient(
            ServerInfo serverInfo,
            Bootstrap clientBootstrap,
            ClientConfig clientConfig,
            ConnectionFactory<I, O> connectionFactory,
            ConnectionHandler<I, O> connectionHandler,
            EventExecutorGroup connHandlingExecutor) {
        if (null == serverInfo) throw new NullPointerException("Server info can not be null.");
        if (null == clientBootstrap) throw new NullPointerException("Client bootstrap can not be null.");
        if (null == clientConfig) throw new NullPointerException("Client config can not be null.");
        if (null == connectionFactory) throw new NullPointerException("Connection factory can not be null.");
        if (null == connectionHandler) throw new NullPointerException("Connection handler can not be null.");
        if (null == connHandlingExecutor) throw new NullPointerException("Conn handling executor can not be null.");

        this.serverInfo = serverInfo;
        this.clientBootstrap = clientBootstrap;
        this.clientConfig = clientConfig;
        this.connectionFactory = connectionFactory;
        this.connectionHandler = connectionHandler;
        this.connHandlingExecutor = connHandlingExecutor;
    }

    public Future<Connection<I, O>> connect() {
        Future<Connection<I, O>> connector = connHandlingExecutor.submit(() -> {
            ChannelFuture f = clientBootstrap.connect(serverInfo.getHost(), serverInfo.getPort());
            f.awaitUninterruptibly();

            if (!f.isDone()) {
                throw new ConnectTimeoutException("连接服务器超时");
            }
            if (f.isCancelled()) {
                throw new CancellationException("连接服务器被取消");
            } else if (!f.isSuccess()) {
                throw (Exception) f.cause();
            }

            Optional<Connection<I, O>> c = connectionHandler.takeOneEstablishedConnection();
            return c.orElse(null);
        });
        connector.addListener(f -> {
            if (f.isCancelled()) {
                log.error("连接服务器被取消");
            } else if (!f.isSuccess()) {
                log.error("连接服务器失败, 失败原因[{}]", f.cause().getMessage());
            }
        });
        return connector;
    }

    protected ChannelInitializer<Channel> newChannelInitializer(
            PipelineConfigurator<I, O> pipelineConfigurator,
            final ConnectionHandler<I, O> connectionHandler,
            final EventExecutorGroup connHandlingExecutor,
            final ClientConfig clientConfig) {
        return new ChannelInitializer<>() {
            @Override
            public void initChannel(Channel ch) throws Exception {
                PipelineConfigurator<I, O> configurator =
                        adaptPipelineConfigurator(
                                pipelineConfigurator, connectionHandler, connHandlingExecutor, clientConfig);
                configurator.configureNewPipeline(ch.pipeline());
            }
        };
    }

    protected PipelineConfigurator<I, O> adaptPipelineConfigurator(
            PipelineConfigurator<I, O> pipelineConfigurator,
            final ConnectionHandler<I, O> connectionHandler,
            final EventExecutorGroup connHandlingExecutor,
            final ClientConfig clientConfig) {
        PipelineConfigurator<I, O> clientRequiredConfigurator;

        if (clientConfig.isReadTimeoutSet()) {
            clientRequiredConfigurator = new PipelineConfiguratorComposite<>(
                    new ClientRequiredConfigurator<>(connectionHandler, connectionFactory, connHandlingExecutor));
        } else {
            clientRequiredConfigurator =
                    new ClientRequiredConfigurator<>(connectionHandler, connectionFactory, connHandlingExecutor);
        }

        if (pipelineConfigurator == null) {
            pipelineConfigurator = clientRequiredConfigurator;
        } else {
            pipelineConfigurator = new PipelineConfiguratorComposite<>(pipelineConfigurator, clientRequiredConfigurator);
        }
        return pipelineConfigurator;
    }
}
