package ai.sangmado.gbclient.jt808.client;

import ai.sangmado.gbclient.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbcodec.jt808.codec.JT808MessageCodec;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import io.netty.channel.ChannelPipeline;

/**
 * JT808 客户端管道配置
 *
 * @param <I> 业务请求
 * @param <O> 业务回复
 */
public class JT808ClientPipelineConfigurator<I, O> implements PipelineConfigurator<I, O> {
    private final ISpecificationContext ctx;

    public JT808ClientPipelineConfigurator(ISpecificationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void configureNewPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new JT808MessageCodec(ctx));
    }
}
