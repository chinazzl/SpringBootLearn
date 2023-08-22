package org.template.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.template.dto.QueryBO;
import org.template.dto.ResultResponse;
import org.template.service.BasicQuery;

import javax.annotation.Resource;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月16日 15:31
 * @Description:
 **********************************/
@RestController
public class CmdbTagController {
    @Resource
    private BasicQuery basicQuery;

    @RequestMapping("/getCmdb")
    public ResultResponse getCmdb(@RequestBody QueryBO queryBO) {
        return basicQuery.basicQueryResult(queryBO);
    }
}
