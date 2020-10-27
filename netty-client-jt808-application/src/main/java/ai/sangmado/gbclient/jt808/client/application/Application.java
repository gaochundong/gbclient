package ai.sangmado.gbclient.jt808.client.application;

import ai.sangmado.gbclient.common.channel.Connection;
import ai.sangmado.gbclient.jt808.client.*;
import ai.sangmado.gbclient.jt808.client.application.connector.JT808ConnectionListener;
import ai.sangmado.gbclient.jt808.client.application.domain.JT808MessageConsumer;
import ai.sangmado.gbclient.jt808.client.application.domain.JT808MessageHandlerMapping;
import ai.sangmado.gbclient.jt808.client.application.domain.handler.jt1078.JT1078_Message_Handler_0x9102;
import ai.sangmado.gbclient.jt808.client.application.domain.handler.jt808.JT808_Message_Handler_0x8001;
import ai.sangmado.gbclient.jt808.client.dispatch.JT808MessageDispatcher;
import ai.sangmado.gbclient.jt808.client.utils.GlobalSerialNumberIssuer;
import ai.sangmado.gbclient.jt808.client.utils.Jackson;
import ai.sangmado.gbprotocol.gbcommon.memory.PooledByteArrayFactory;
import ai.sangmado.gbprotocol.jt1078.protocol.enums.JT1078MessageId;
import ai.sangmado.gbprotocol.jt1078.protocol.enums.OperationResult;
import ai.sangmado.gbprotocol.jt1078.protocol.message.content.JT1078_Message_Content_0x1005;
import ai.sangmado.gbprotocol.jt1078.protocol.message.content.JT1078_Message_Content_0x1206;
import ai.sangmado.gbprotocol.jt1078.protocol.message.extension.JT1078MessageExtension;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.IVersionedSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolVersionedSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.*;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessageAssembler;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.*;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x0200_Additional.*;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeader;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeaderFactory;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * JT808 业务客户端应用程序
 */
@Slf4j
@SuppressWarnings({"InfiniteLoopStatement"})
public class Application {

    public static void main(String[] args) {
        // 协议上下文仅与协议报文序列化和反序列化过程相关
        ISpecificationContext ctx = new JT808ProtocolSpecificationContext()
                .withBufferPool(new PooledByteArrayFactory(512, 10));

        // 通过环境变量加载服务器参数
        final String ENV_JT808_SERVER_HOST = "JT808_SERVER_HOST";
        final String ENV_JT808_SERVER_PORT = "JT808_SERVER_PORT";

        String envHostValue = System.getenv(ENV_JT808_SERVER_HOST);
        String host = !Strings.isNullOrEmpty(envHostValue) ? envHostValue : "localhost";
        String envPortValue = System.getenv(ENV_JT808_SERVER_PORT);
        int port = !Strings.isNullOrEmpty(envPortValue) ? Integer.parseInt(envPortValue) : 7200;

        // 加载JT1078协议消息扩展
        JT1078MessageExtension.extend();

        // 注册业务域消息处理器, 此处可应用IoC容器自动发现机制或者类反射扫描机制等进行处理器映射
        JT808MessageHandlerMapping messageHandlerMapping = new JT808MessageHandlerMapping();
        messageHandlerMapping.addHandler(new JT808_Message_Handler_0x8001(ctx));
        messageHandlerMapping.addHandler(new JT1078_Message_Handler_0x9102(ctx));

        // 构建客户端对象
        JT808MessageConsumer messageConsumer = new JT808MessageConsumer(messageHandlerMapping.getHandlers());
        JT808MessageDispatcher messageDispatcher = new JT808MessageDispatcher().bindSubscriber(messageConsumer);
        JT808ConnectionHandler connectionHandler = new JT808ConnectionHandler();
        JT808MessageProcessor messageProcessor = new JT808MessageProcessor(connectionHandler, messageDispatcher);
        JT808ClientPipelineConfigurator pipelineConfigurator = new JT808ClientPipelineConfigurator(ctx, messageProcessor);
        JT808ClientBuilder clientBuilder = new JT808ClientBuilder(host, port, connectionHandler, pipelineConfigurator);
        JT808Client client = clientBuilder.build();

        // 尝试建立连接
        JT808ConnectionListener connectionListener = new JT808ConnectionListener();
        connectionHandler.subscribe(connectionListener);
        Connection<JT808Message, JT808Message> connection = null;
        try {
            int retryCount = 1;
            int retryLimit = 30;
            while (retryCount <= retryLimit) {
                log.info("尝试连接服务器中, 尝试次数[{}]...", retryCount);
                client.connect();

                // 连接的过程是异步的，在此同步等待
                try {
                    int waitTimeInMillis = 5000;
                    log.info("开始等待, 等待时长[{}s]", waitTimeInMillis / 1000);
                    for (int i = 0; i < waitTimeInMillis / 1000; i++) {
                        connection = connectionListener.getEstablishedConnectionOrNull();
                        if (connection != null) break;
                        Thread.sleep(1000);
                    }
                    log.info("等待完毕");
                } catch (InterruptedException ignored) {
                }

                connection = connectionListener.getEstablishedConnectionOrNull();
                if (connection == null) {
                    log.info("继续重试连接");
                    retryCount++;
                } else {
                    log.info("连接服务器成功");
                    break;
                }
            }
        } catch (Exception ex) {
            log.error("连接服务器异常", ex);
        }

        // 未能连接服务器, 程序退出
        if (connection == null) {
            log.error("未能连接服务器, 程序退出");
            return;
        }

        // 读取输入参数，发送测试消息
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String inputString = scanner.nextLine();
            log.info("输入参数: " + inputString);
            try {
                IVersionedSpecificationContext newCtx = JT808ProtocolVersionedSpecificationContext.newInstance()
                        .withProtocolVersion(JT808ProtocolVersion.V2013)
                        .withByteOrder(ctx.getByteOrder())
                        .withCharset(ctx.getCharset())
                        .withBufferPool(ctx.getBufferPool());
                JT808Message packet = null;
                switch (inputString) {
                    case "0x0001": { // 终端通用应答
                        packet = create_JT808_Message_0x0001_packet(newCtx);
                        break;
                    }
                    case "0x0100": { // 终端注册
                        packet = create_JT808_Message_0x0100_packet(newCtx);
                        break;
                    }
                    case "0x0102": { // 终端鉴权
                        packet = create_JT808_Message_0x0102_packet(newCtx);
                        break;
                    }
                    case "0x0002": { // 终端心跳
                        packet = create_JT808_Message_0x0002_packet(newCtx);
                        break;
                    }
                    case "0x0003": { // 终端注销
                        packet = create_JT808_Message_0x0003_packet(newCtx);
                        break;
                    }
                    case "0x0004": { // 终端查询服务器时间请求
                        packet = create_JT808_Message_0x0004_packet(newCtx);
                        break;
                    }
                    case "0x0200": { // 终端位置信息汇报
                        packet = create_JT808_Message_0x0200_packet(newCtx);
                        break;
                    }
                    case "0x1005": { // 终端上传乘客流量 - JT1078
                        packet = create_JT1078_Message_0x1005_packet(newCtx);
                        break;
                    }
                    case "0x1206": { // 终端文件上传完成通知 - JT1078
                        packet = create_JT1078_Message_0x1206_packet(newCtx);
                        break;
                    }
                }
                if (packet != null && connection.isActive()) {
                    logPacket(connection, packet);
                    connection.writeAndFlush(packet);
                }
            } catch (Exception ex) {
                log.error("向服务器发送消息失败", ex);
            }
        }
    }

    private static void logPacket(Connection<JT808Message, JT808Message> connection, JT808Message packet) {
        String json = Jackson.toJsonPrettyString(packet);
        log.info("向服务器发送消息, 消息ID[{}], 消息名称[{}], 协议版本[{}], 连接ID[{}]{}{}",
                packet.getMessageId().getName(),
                packet.getMessageId().getDescription(),
                packet.getProtocolVersion().getName(),
                connection.getConnectionId(),
                System.lineSeparator(), json);
    }

    // 终端通用应答
    private static JT808Message create_JT808_Message_0x0001_packet(IVersionedSpecificationContext ctx) {
        JT808MessageId messageId = JT808MessageId.JT808_Message_0x0001;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next(100);

        // 回复平台成功
        int ackSerialNumber = 301;
        JT1078MessageId ackId = JT1078MessageId.JT1078_Message_0x9101;
        JT808DeviceCommonReplyResult result = JT808DeviceCommonReplyResult.Success;

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT808_Message_Content_0x0001.builder()
                .ackSerialNumber(ackSerialNumber)
                .ackId(ackId)
                .result(result)
                .build();

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }

    // 终端注册
    private static JT808Message create_JT808_Message_0x0100_packet(IVersionedSpecificationContext ctx) {
        JT808MessageId messageId = JT808MessageId.JT808_Message_0x0100;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next(100);

        int provinceId = 111;
        int cityId = 222;
        String manufacturerId = "333";
        String deviceId = "8888888";
        String deviceModel = "Mock";
        String plateNumber = "京A88888";
        JT808VehiclePlateColor plateColor = JT808VehiclePlateColor.JT808_Vehicle_Color_Yellow;

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

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }

    // 终端鉴权
    private static JT808Message create_JT808_Message_0x0102_packet(IVersionedSpecificationContext ctx) {
        JT808MessageId messageId = JT808MessageId.JT808_Message_0x0102;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next(100);

        String authCode = "6eaf001e-b543-11ea-a56b-02641672dd7e";
        String deviceImei = "888888888888888";
        String softwareVersion = "1.2.3";

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT808_Message_Content_0x0102.builder()
                .authCodeLength(authCode.length())
                .authCode(authCode)
                .deviceImei(deviceImei)
                .softwareVersion(softwareVersion)
                .build();

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }

    // 终端心跳
    private static JT808Message create_JT808_Message_0x0002_packet(IVersionedSpecificationContext ctx) {
        JT808MessageId messageId = JT808MessageId.JT808_Message_0x0002;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next(100);

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT808_Message_Content_0x0002.builder()
                .build();

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }

    // 终端注销
    private static JT808Message create_JT808_Message_0x0003_packet(IVersionedSpecificationContext ctx) {
        JT808MessageId messageId = JT808MessageId.JT808_Message_0x0003;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next(100);

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT808_Message_Content_0x0003.builder()
                .build();

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }

    // 终端查询服务器时间请求
    private static JT808Message create_JT808_Message_0x0004_packet(IVersionedSpecificationContext ctx) {
        JT808MessageId messageId = JT808MessageId.JT808_Message_0x0004;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next(100);

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT808_Message_Content_0x0004.builder()
                .build();

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }

    // 终端位置信息汇报
    private static JT808Message create_JT808_Message_0x0200_packet(IVersionedSpecificationContext ctx) {
        JT808MessageId messageId = JT808MessageId.JT808_Message_0x0200;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next(100);

        JT808_Message_Content_0x0200_AI_0x01 info1 = new JT808_Message_Content_0x0200_AI_0x01();
        info1.setMilometer(88);
        JT808_Message_Content_0x0200_AI_0x02 info2 = new JT808_Message_Content_0x0200_AI_0x02();
        info2.setFuelMeter(100);
        JT808_Message_Content_0x0200_AI_0x03 info3 = new JT808_Message_Content_0x0200_AI_0x03();
        info3.setSpeed(66);
        JT808_Message_Content_0x0200_AI_0x04 info4 = new JT808_Message_Content_0x0200_AI_0x04();
        info4.setWarningId(23);
        List<JT808_Message_Content_0x0200_AdditionalInformation> additionalInformationList = new ArrayList<>(4);
        additionalInformationList.add(info1);
        additionalInformationList.add(info2);
        additionalInformationList.add(info3);
        additionalInformationList.add(info4);

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT808_Message_Content_0x0200.builder()
                .basicInformation(
                        JT808_Message_Content_0x0200.BasicInformation.builder()
                                .warningType(JT808WarningType.JT808_Warning_1)
                                .state(JT808VehicleState.JT808_Vehicle_State_19)
                                .latitude(333L)
                                .longitude(22L)
                                .altitude(4)
                                .speed(60)
                                .direction(18)
                                .timestamp(LocalDateTime.now(ZoneId.of("UTC+08:00")))
                                .build())
                .additionalInformationList(additionalInformationList)
                .build();

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }

    // 终端上传乘客流量
    private static JT808Message create_JT1078_Message_0x1005_packet(IVersionedSpecificationContext ctx) {
        JT1078MessageId messageId = JT1078MessageId.JT1078_Message_0x1005;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next(100);

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT1078_Message_Content_0x1005.builder()
                .beginTime(LocalDateTime.now(ZoneId.of("UTC+08:00")).minusHours(1))
                .endTime(LocalDateTime.now(ZoneId.of("UTC+08:00")))
                .numberOfPeopleGettingOn(333)
                .numberOfPeopleGettingOff(322)
                .build();

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }

    // 终端文件上传完成通知
    private static JT808Message create_JT1078_Message_0x1206_packet(IVersionedSpecificationContext ctx) {
        JT1078MessageId messageId = JT1078MessageId.JT1078_Message_0x1206;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next(100);

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT1078_Message_Content_0x1206.builder()
                .ackSerialNumber(serialNumber)
                .result(OperationResult.Failed)
                .build();

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }
}
