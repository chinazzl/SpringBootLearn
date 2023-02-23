package org.drools.demo.controller;

import org.drools.demo.Entity.bo.BaselineBO;
import org.drools.demo.common.Result;
import org.drools.demo.dao.IBaselineDao;
import org.drools.demo.service.IBaselineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2023/2/14 17:14
 * @Description:
 */
@Controller
@RequestMapping("/cpu")
public class BaelineController {

    @Autowired
    @Qualifier("baslineVer2")
    private IBaselineService baselineService;

    @RequestMapping("/index")
    public String index() {
        return "baseline";
    }

    /**
     * {
     * "xAxis": [
     * "xxx",
     * "xxx"
     * ],
     * "series": {
     * "upTline": [],
     * "downTline": [],
     * "upBaseline": [],
     * "downBaseline": []
     * }
     * }
     */
    @RequestMapping("/getbaseline")
    @ResponseBody
    public Result<BaselineBO> getbaseline(@RequestParam("ip") String ip) {
        // String ip = "102.200.218.193";
        BaselineBO cpuBaseline = baselineService.getCpuBaseline(ip);

        return Result.ok(cpuBaseline);
    }
}
