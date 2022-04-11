package org.drools.demo.controller;

import org.drools.demo.common.Result;
import org.drools.demo.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author Julyan
 * @version V1.0
 * @Description:
 * @Date: 2022/3/25 14:48
 */
@Controller
@RequestMapping("/rules")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @RequestMapping(value = "/simpleRule")
    public String simpleRule() {
        return "index";
    }

    @RequestMapping(value = "/getRule",method = RequestMethod.POST)
    @ResponseBody
    public Result getRule(@RequestBody Map<String,String> json) {
        System.out.println(json);
        ruleService.rule();
        return Result.ok(null);
    }
}
