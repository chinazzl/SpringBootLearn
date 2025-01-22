package com.simpleWeb.handler;

import com.simpleWeb.entity.TrapSourceConfig;
import org.snmp4j.smi.VariableBinding;

/**
 * @author: zhaolin
 * @Date: 2025/1/22
 * @Description:
 **/
public interface TrapHandler {
    String getOid();
    void handle(VariableBinding binding, TrapSourceConfig.TrapSource source);
}
