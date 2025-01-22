package com.simpleWeb.handler;

import com.simpleWeb.entity.TrapSource;
import com.simpleWeb.entity.TrapSourceConfig;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Vector;

/**
 * @author: zhaolin
 * @Date: 2025/1/22
 * @Description:
 **/
@Component
@Slf4j
public class SourceSpecificTrapHandler implements CommandResponder {
    private final TrapSource source;
    private final List<TrapHandler> handlers;

    public SourceSpecificTrapHandler(TrapSource source, List<TrapHandler> handlers) {
        this.source = source;
        this.handlers = handlers;
    }

    @Override
    public void processPdu(CommandResponderEvent event) {
        if (event.getPDU().getType() != PDU.TRAP) {
            return;
        }

        PDU pdu = event.getPDU();
        log.info("Received trap from {}: {}", source.getName(),
                event.getPeerAddress());

        try {
            // 验证用户信息
            if (!validateUser(event)) {
                log.warn("Invalid user credentials from {}", source.getName());
                return;
            }

            // 处理Trap消息
            processTrapPdu(pdu);

        } catch (Exception e) {
            log.error("Error processing trap from {}", source.getName(), e);
        }
    }

    private boolean validateUser(CommandResponderEvent event) {
        // 实现用户验证逻辑
        return true; // 简化示例，实际应该验证用户凭证
    }

    private void processTrapPdu(PDU pdu) {
        Vector<? extends VariableBinding> varBinds = (Vector<? extends VariableBinding>) pdu.getVariableBindings();

        for (VariableBinding binding : varBinds) {
            String oid = binding.getOid().toString();

            // 将Trap信息传递给所有相关的处理器
            handlers.stream()
                    .filter(handler -> handler.getOid().equals(oid))
                    .forEach(handler -> {
                        try {
                            handler.handle(binding, source);
                        } catch (Exception e) {
                            log.error("Error in handler for OID {} from {}",
                                    oid, source.getName(), e);
                        }
                    });
        }
    }
}
