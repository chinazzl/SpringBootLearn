package org.drools.demo.controller;

import org.drools.demo.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    @RequestMapping(value = "/simpleRule",method = RequestMethod.POST)
    @ResponseBody
    public String simpleRule(@RequestBody Map<String,String> map) {
        System.out.println(map);
        ruleService.rule();
        return "index";
    }
}
