package ai.sangmado.gbclient.jt808.client.application;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbclient.jt808.client.JT808Client;
import ai.sangmado.gbclient.jt808.client.JT808ClientBuilder;
import ai.sangmado.gbclient.jt808.client.JT808ClientPipelineConfigurator;
import ai.sangmado.gbclient.jt808.client.JT808MessageHandler;
import ai.sangmado.gbclient.jt808.client.utils.GlobalSerialNumberIssuer;
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
import com.google.common.base.Strings;
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
                .withProtocolVersion(JT808ProtocolVersion.V2013)
                .withBufferPool(new PooledByteArrayFactory(512, 10));

        String host = !Strings.isNullOrEmpty(System.getenv("JT808_SERVER_HOST")) ? System.getenv("JT808_SERVER_HOST") : "localhost";
        int port = !Strings.isNullOrEmpty(System.getenv("JT808_SERVER_PORT")) ? Integer.parseInt(System.getenv("JT808_SERVER_PORT")) : 7200;

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

        if (connection == null) {
            log.error("未能连接服务器, 暂无重连机制, 程序退出。");
            return;
        }

        // 读取输入参数，发送测试消息
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String inputString = scanner.nextLine();
            log.info("输入参数: " + inputString);
            try {
                if (inputString.equals("0x0100")) {
                    JT808MessagePacket packet = create_JT808_Message_0x0100_packet(ctx);
                    logPacket(connection, packet);
                    connection.writeAndFlush(packet);
                }
            } catch (Exception ex) {
                log.error("向服务器发送消息失败", ex);
            }
        }
    }

    private static void logPacket(Connection<JT808MessagePacket, JT808MessagePacket> connection, JT808MessagePacket packet) {
        String json = Jackson.toJsonPrettyString(packet);
        log.info("通过连接 [{}] 向服务器发送消息, 协议版本[{}], 消息ID[{}]{}{}",
                connection.getConnectionId(),
                packet.getProtocolVersion().getName(), packet.getMessageId().getName(),
                System.lineSeparator(), json);
    }

    private static JT808MessagePacket create_JT808_Message_0x0100_packet(ISpecificationContext ctx) {
        JT808MessageId messageId = JT808MessageId.JT808_Message_0x0100;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next();

        int provinceId = 111;
        int cityId = 222;
        String manufacturerId = "333";
        String deviceId = "8888888";
        String deviceModel = "Mock";
        String plateNumber = "京A88888";
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
