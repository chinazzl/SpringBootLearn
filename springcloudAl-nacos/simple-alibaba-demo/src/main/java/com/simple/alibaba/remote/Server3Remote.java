package com.simple.alibaba.remote;

import com.simple.alibaba.config.remoteFallback.Server3RemoteFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**********************************
 * @author zhang zhao lin
 * @date 2022年09月11日 12:27
 * @Description:
 **********************************/
@FeignClient(value = "server-3",fallbackFactory = Server3RemoteFallbackFactory.class)
public interface Server3Remote {

    @RequestMapping(value = "/s3/query", method = RequestMethod.GET)
    String query(@RequestParam(value = "q") String condition);
}
