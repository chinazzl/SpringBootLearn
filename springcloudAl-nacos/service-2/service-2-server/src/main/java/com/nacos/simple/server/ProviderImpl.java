package com.nacos.simple.server;

import com.nacos.serice.Provider;
import org.apache.dubbo.config.annotation.Service;

/**********************************
 * @author zhang zhao lin
 * @date 2022年08月28日 22:17
 * @Description:
 **********************************/
@Service
public class ProviderImpl implements Provider {
    @Override
    public String getProvider() {
        return "service2 server provide message";
    }
}
