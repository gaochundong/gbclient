package ai.sangmado.gbclient.jt808.client;

import ai.sangmado.gbclient.common.channel.Connection;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JT808Client_Test {

    private IBufferPool bufferPool = new PooledByteArrayFactory(512, 10);
    private ISpecificationContext ctx = new JT808ProtocolSpecificationContext().withBufferPool(bufferPool);

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void when_jt808_client_start_then_should_send_message() {
        JT808MessagePacket packet = create_JT808_Message_0x0100_packet();

        String host = "localhost";
        int port = 7200;
        JT808ClientBuilder<JT808MessagePacket, JT808MessagePacket> clientBuilder = new JT808ClientBuilder<>(ctx, host, port);
        JT808Client<JT808MessagePacket, JT808MessagePacket> client = clientBuilder.build();
        Connection<JT808MessagePacket, JT808MessagePacket> connection = client.connect();
        connection.write(packet);
    }

    private JT808MessagePacket create_JT808_Message_0x0100_packet() {
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
        assertEquals(1, packets.size());

        return packets.get(0);
    }
}