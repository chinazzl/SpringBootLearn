package com.simple.alibaba.config.remoteFallback;

import com.simple.alibaba.remote.Server3Remote;
import feign.hystrix.FallbackFactory;

/**********************************
 * @author zhang zhao lin
 * @date 2022年09月12日 10:46
 * @Description:
 **********************************/
public class Server3RemoteFallbackFactory implements FallbackFactory<Server3Remote> {

    @Override
    public Server3Remote create(Throwable throwable) {
        return new Server3Remote() {
            @Override
            public String query(String condition) {
                if (throwable instanceof RuntimeException) {
                    System.out.println("出现异常了：" + throwable.getMessage());
                }
                return "remote3 已经熔断";
            }
        };
    }
}
