package ai.sangmado.gbclient.jt808.client;

import ai.sangmado.gbclient.common.client.AbstractClientBuilder;
import ai.sangmado.gbclient.common.client.ServerInfo;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import io.netty.bootstrap.Bootstrap;

/**
 * JT808 业务客户端构造器
 *
 * @param <I> 读取 JT808 业务消息
 * @param <O> 写入 JT808 业务消息
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class JT808ClientBuilder<I, O> extends AbstractClientBuilder<I, O, JT808ClientBuilder<I, O>, JT808Client<I, O>> {
    private final ISpecificationContext ctx;

    public JT808ClientBuilder(ISpecificationContext ctx, String host, int port) {
        super(new ServerInfo(host, port));
        this.ctx = ctx;
    }

    public JT808ClientBuilder(ISpecificationContext ctx, String host, int port, Bootstrap bootstrap) {
        super(new ServerInfo(host, port), bootstrap);
        this.ctx = ctx;
    }

    @Override
    protected JT808Client<I, O> createClient() {
        this.pipelineConfigurator(new JT808ClientPipelineConfigurator<>(this.ctx));
        return new JT808Client<>(this.ctx, serverInfo, clientBootstrap, pipelineConfigurator, clientConfig, channelFactory, connectionFactory);
    }
}
