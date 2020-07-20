package ai.sangmado.gbclient.jt808.client;

import ai.sangmado.gbclient.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbcodec.jt808.codec.JT808DelimiterBasedFrameDecoder;
import ai.sangmado.gbcodec.jt808.codec.JT808MessageCodec;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * JT808 客户端管道配置
 */
@Slf4j
public class JT808ClientPipelineConfigurator<I extends JT808MessagePacket, O extends JT808MessagePacket> implements PipelineConfigurator<I, O> {
    private final ISpecificationContext ctx;
    private final MessageToMessageDecoder<O> messageProcessor;

    public JT808ClientPipelineConfigurator(ISpecificationContext ctx, MessageToMessageDecoder<O> messageProcessor) {
        this.ctx = ctx;
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void configureNewPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("JT808消息分割器", new JT808DelimiterBasedFrameDecoder());
        pipeline.addLast("JT808消息编解码器", new JT808MessageCodec(ctx));
        pipeline.addLast("JT808消息处理器", messageProcessor);
    }
}
