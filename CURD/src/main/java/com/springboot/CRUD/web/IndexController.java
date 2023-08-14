package com.springboot.CRUD.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/d")
public class IndexController {

    @RequestMapping("/i")
    public ModelAndView mv(){
        ModelAndView mov = new ModelAndView();
        mov.addObject("name","heh");
        mov.setViewName("hello.html");
        return mov;
    }
}
