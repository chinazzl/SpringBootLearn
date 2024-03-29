package org.drools.demo.controller;

import org.apache.commons.lang3.StringUtils;
import org.drools.demo.Entity.bo.StudentBO;
import org.drools.demo.Entity.vo.StudentVO;
import org.drools.demo.annotation.RateLimiter;
import org.drools.demo.common.Result;
import org.drools.demo.enums.LimitType;
import org.drools.demo.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

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

    @RateLimiter(time = 30,count = 5,limitType = LimitType.IP)
    @RequestMapping(value = "/simpleRule")
    public String simpleRule() {
        return "index";
    }

    @RequestMapping(value = "/getRule", method = RequestMethod.POST)
    @ResponseBody
    public Result getRule(@RequestBody StudentBO student) {
        System.out.println(student);
        List<StudentVO> ruleList = ruleService.rule(capsulationRule(student));
        return Result.ok(ruleList);
    }

    private String capsulationRule(StudentBO student) {
        StringBuilder sb = new StringBuilder("(");
        String compareAgeFlag = student.getCompareAgeFlag();
        String name = Optional.ofNullable(student.getName()).filter(StringUtils::isNotBlank).map(n -> "name ==\"" + n + "\"&& ").orElse("");
        sb.append(name);
        int minAge = Optional.of(student.getMinAge()).orElse(0);
        int maxAge = Optional.of(student.getMaxAge()).orElse(0);
        String compareAge = "";
        if (minAge > 0) {
            if ("1".equals(compareAgeFlag)) {
                compareAge = " age >= " + minAge;
            } else if ("2".equals(compareAgeFlag)) {
                compareAge = " age<= " + minAge;
            } else if ("3".equals(compareAgeFlag)) {
                compareAge = " age== " + minAge;
            } else if ("4".equals(compareAgeFlag)) {
                compareAge = "age >= " + minAge + " && " + " age <=" + maxAge;
            }
            sb.append(compareAge).append(")");
        }
        return sb.toString();
    }
}
