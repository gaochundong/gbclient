package ai.sangmado.gbclient.jt808.client.application;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbclient.jt808.client.JT808Client;
import ai.sangmado.gbclient.jt808.client.JT808ClientBuilder;
import ai.sangmado.gbclient.jt808.client.JT808ClientPipelineConfigurator;
import ai.sangmado.gbclient.jt808.client.JT808MessageHandler;
import ai.sangmado.gbprotocol.gbcommon.memory.IBufferPool;
import ai.sangmado.gbprotocol.gbcommon.memory.PooledByteArrayFactory;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacketBuilder;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808MessageContent;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x0100;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeader;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeaderFactory;

import java.io.IOException;
import java.util.List;

/**
 * JT808 业务客户端应用程序
 */
@SuppressWarnings("InfiniteLoopStatement")
public class Application {
    public static void main(String[] args) {
        IBufferPool bufferPool = new PooledByteArrayFactory(512, 10);
        ISpecificationContext ctx = new JT808ProtocolSpecificationContext().withBufferPool(bufferPool);
        String host = "localhost";
        int port = 7200;
        JT808MessageHandler<JT808MessagePacket, JT808MessagePacket> messageHandler = new JT808MessageHandler<>(ctx);
        JT808ClientPipelineConfigurator<JT808MessagePacket, JT808MessagePacket> pipelineConfigurator = new JT808ClientPipelineConfigurator<>(ctx, messageHandler);
        JT808ClientBuilder<JT808MessagePacket, JT808MessagePacket> clientBuilder = new JT808ClientBuilder<>(ctx, host, port, pipelineConfigurator);
        JT808Client<JT808MessagePacket, JT808MessagePacket> client = clientBuilder.build();
        Connection<JT808MessagePacket, JT808MessagePacket> connection = null;
        try {
            connection = client.connect();
            if (connection != null) {
                messageHandler.notifyConnectionConnected(connection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JT808MessagePacket packet = create_JT808_Message_0x0100_packet(ctx);

            if (connection != null) {
                connection.writeAndFlush(packet);
            }

            System.out.println("Client is connected.");
            while (true) {
                int value = System.in.read();
                System.out.println(value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JT808MessagePacket create_JT808_Message_0x0100_packet(ISpecificationContext ctx) {
        JT808MessageId messageId = JT808MessageId.JT808_Message_0x0100;
        String phoneNumber = "123456";
        int serialNumber = 123;

        int provinceId = 19;
        int cityId = 18;
        String manufacturerId = "777";
        String deviceId = "111";
        String deviceModel = "Made in China";
        String plateNumber = "AAA-BBB-CCC";
        Integer plateColor = 8;

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT808_Message_Content_0x0100.builder()
                .provinceId(provinceId)
                .cityId(cityId)
                .manufacturerId(manufacturerId)
                .deviceId(deviceId)
                .deviceModel(deviceModel)
                .plateNumber(plateNumber)
                .plateColor(plateColor)
                .build();

        List<JT808MessagePacket> packets = JT808MessagePacketBuilder.buildPackets(ctx, header, content);
        return packets.get(0);
    }
}
