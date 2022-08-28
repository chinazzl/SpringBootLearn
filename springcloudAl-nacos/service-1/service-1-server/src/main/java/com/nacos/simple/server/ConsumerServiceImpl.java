package com.nacos.simple.server;

import com.nacos.api.ConsumerService;
import com.nacos.serice.Provider;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;

/**********************************
 * @author zhang zhao lin
 * @date 2022年08月28日 13:15
 * @Description:
 **********************************/
// 使用dubbo 注解service
@Service
public class ConsumerServiceImpl implements ConsumerService {

    @Reference
    private Provider provide;
    @Override
    public String consumerserver1() {
        String provider = provide.getProvider();
        return "this is server-1 consumer server recieved message =>" + provider ;
    }
}
