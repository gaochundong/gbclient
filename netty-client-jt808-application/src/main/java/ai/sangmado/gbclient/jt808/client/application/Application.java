package ai.sangmado.gbclient.jt808.client.application;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbclient.jt808.client.JT808Client;
import ai.sangmado.gbclient.jt808.client.JT808ClientBuilder;
import ai.sangmado.gbclient.jt808.client.JT808ClientPipelineConfigurator;
import ai.sangmado.gbclient.jt808.client.JT808MessageHandler;
import ai.sangmado.gbclient.jt808.client.utils.Jackson;
import ai.sangmado.gbprotocol.gbcommon.memory.PooledByteArrayFactory;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808ProtocolVersion;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacketBuilder;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808MessageContent;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x0100;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeader;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeaderFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Scanner;

/**
 * JT808 业务客户端应用程序
 */
@Slf4j
@SuppressWarnings("InfiniteLoopStatement")
public class Application {

    public static void main(String[] args) {
        ISpecificationContext ctx = new JT808ProtocolSpecificationContext()
                .withProtocolVersion(JT808ProtocolVersion.V2019)
                .withBufferPool(new PooledByteArrayFactory(512, 10));

        String host = "localhost";
        int port = 7200;
        JT808MessageHandler<JT808MessagePacket, JT808MessagePacket> messageHandler = new JT808MessageHandler<>(ctx);
        JT808ClientPipelineConfigurator<JT808MessagePacket, JT808MessagePacket> pipelineConfigurator = new JT808ClientPipelineConfigurator<>(ctx, messageHandler);
        JT808ClientBuilder<JT808MessagePacket, JT808MessagePacket> clientBuilder = new JT808ClientBuilder<>(ctx, host, port, pipelineConfigurator);
        JT808Client<JT808MessagePacket, JT808MessagePacket> client = clientBuilder.build();

        Connection<JT808MessagePacket, JT808MessagePacket> connection = null;
        try {
            log.info("连接服务器中...");
            connection = client.connect();
            messageHandler.notifyConnectionConnected(connection);
            log.info("与服务器建立连接成功");
        } catch (Exception ex) {
            log.error("连接服务器失败", ex);
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String inputString = scanner.nextLine();
            log.info("输入参数: " + inputString);
            if (inputString.equals("0x0100")) {
                try {
                    JT808MessagePacket packet = create_JT808_Message_0x0100_packet(ctx);
                    if (connection != null) {
                        String json = Jackson.toJsonPrettyString(packet);
                        log.info("通过连接 [{}] 向服务器发送消息, 协议版本[{}], 消息ID[{}]{}{}",
                                connection.getConnectionId(),
                                packet.getProtocolVersion().getName(), packet.getMessageId().getName(),
                                System.lineSeparator(), json);
                        connection.writeAndFlush(packet);
                    }
                } catch (Exception ex) {
                    log.error("向服务器发送消息失败", ex);
                }
            }
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
