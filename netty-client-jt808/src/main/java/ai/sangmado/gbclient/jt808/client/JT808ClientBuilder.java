package ai.sangmado.gbclient.jt808.client;

import ai.sangmado.gbclient.common.client.AbstractClientBuilder;
import ai.sangmado.gbclient.common.client.ServerInfo;
import ai.sangmado.gbclient.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import io.netty.bootstrap.Bootstrap;
import lombok.extern.slf4j.Slf4j;

/**
 * JT808 业务客户端构造器
 */
@Slf4j
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class JT808ClientBuilder<I extends JT808MessagePacket, O extends JT808MessagePacket> extends AbstractClientBuilder<I, O, JT808ClientBuilder<I, O>, JT808Client<I, O>> {
    private final ISpecificationContext ctx;

    public JT808ClientBuilder(ISpecificationContext ctx, String host, int port, PipelineConfigurator<O, I> pipelineConfigurator) {
        super(new ServerInfo(host, port));
        this.ctx = ctx;
        this.pipelineConfigurator(pipelineConfigurator);
    }

    public JT808ClientBuilder(ISpecificationContext ctx, String host, int port, PipelineConfigurator<O, I> pipelineConfigurator, Bootstrap bootstrap) {
        super(new ServerInfo(host, port), bootstrap);
        this.ctx = ctx;
        this.pipelineConfigurator(pipelineConfigurator);
    }

    @Override
    protected JT808Client<I, O> createClient() {
        return new JT808Client<>(this.ctx, serverInfo, clientBootstrap, pipelineConfigurator, clientConfig, channelFactory, connectionFactory);
    }
}
