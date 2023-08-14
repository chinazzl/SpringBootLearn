package com.server3.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

/**********************************
 * @author zhang zhao lin
 * @date 2022年09月11日 12:25
 * @Description:
 **********************************/
@RestController
@RequestMapping("/s3")
public class QueryController {

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public String query(@RequestParam("q") String query) {
        try {
            if ("LiLei".equals(query)) {
                TimeUnit.MILLISECONDS.sleep(500);
            } else {
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "query param:" + query;
    }
}
