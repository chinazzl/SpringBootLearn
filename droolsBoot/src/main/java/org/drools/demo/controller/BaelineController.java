package org.drools.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2023/2/14 17:14
 * @Description:
 */
@Controller
@RequestMapping("/cpu")
public class BaelineController {

    @RequestMapping("/index")
    public String index() {
        return "baseline";
    }

}
