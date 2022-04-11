package org.drools.demo.controller;

import org.drools.demo.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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

    @RequestMapping("/simpleRule")
    public String simpleRule(ModelAndView modelAndView) {
        ruleService.rule();
        return "index";
    }
}
