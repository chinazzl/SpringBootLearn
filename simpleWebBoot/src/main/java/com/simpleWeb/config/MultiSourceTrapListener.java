package com.simpleWeb.config;

import com.simpleWeb.entity.TrapSourceConfig;
import com.simpleWeb.handler.TrapHandler;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.*;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: zhaolin
 * @Date: 2025/1/22
 * @Description:
 **/
@Slf4j
@Component
public class MultiSourceTrapListener {

    private final Map<String, Snmp> snmpSessions = new ConcurrentHashMap<>();
    private final List<TrapHandler> trapHandlers;

    public MultiSourceTrapListener(TrapSourceConfig config, List<TrapHandler> trapHandlers) {
        this.trapHandlers = trapHandlers;
        initializeListeners(config.getSources());
    }

    private void initializeListeners(List<TrapSourceConfig.TrapSource> sources) {
        sources.forEach(this::createListener);
    }

    private void createListener(TrapSourceConfig.TrapSource source) {
        try {
            // 创建 SNMP V3 的 USM
            USM usm = new USM(SecurityProtocols.getInstance(),
                    new OctetString(MPv3.createLocalEngineID()), 0);
            SecurityModels.getInstance().addSecurityModel(usm);

            // 创建Transport
            Address targetAddress = new UdpAddress(source.getIp() + "/" + source.getPort());
            TransportMapping<?> transport = new DefaultUdpTransportMapping();

            // 创建 SNMP 实例
            Snmp snmp = new Snmp(transport);

            // 添加用户到 USM
            UsmUser user = new UsmUser(
                    new OctetString(source.getUsername()),
                    AuthMD5.ID,
                    new OctetString(source.getPassword()),
                    PrivDES.ID,
                    new OctetString(source.getAuthPassword())
            );
            snmp.getUSM().addUser(
                    new OctetString(source.getUsername()),
                    user
            );

            // 添加命令响应处理器
            snmp.addCommandResponder(new SourceSpecificTrapHandler(source, trapHandlers));

            // 启动监听
            transport.listen();
            snmpSessions.put(source.getName(), snmp);

            log.info("Started SNMP trap listener for {}: {}:{}",
                    source.getName(), source.getIp(), source.getPort());

        } catch (IOException e) {
            log.error("Failed to create SNMP listener for {}", source.getName(), e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void shutdown() {
        snmpSessions.forEach((name, snmp) -> {
            try {
                snmp.close();
            } catch (IOException e) {
                log.error("Error closing SNMP session for {}", name, e);
            }
        });
    }
}
