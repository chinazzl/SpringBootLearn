package com.simple.alibaba.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**********************************
 * @author zhang zhao lin
 * @date 2022年09月09日 23:43
 * @Description:
 **********************************/
@FeignClient(name="github",url = "https://api.github.com/search")
public interface GitHubRemote {

    /**
     * "<a href="https://api.github.com/search/repositories?q=query}{&page,per_page,sort,order}"">...</a>{
     */
    @GetMapping("/repositories")
    String search(@RequestParam("q") String query,@RequestParam("per_page") String perPage);
}
