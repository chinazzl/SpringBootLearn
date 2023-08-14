package com.simple.alibaba.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.simple.alibaba.remote.GitHubRemote;
import com.simple.alibaba.remote.Server3Remote;
import com.simple.alibaba.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**********************************
 * @author zhang zhao lin
 * @date 2022年09月09日 23:42
 * @Description:
 **********************************/
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private GitHubRemote gitHubRemote;

    @Autowired
    private Server3Remote server3Remote;

    @Autowired
    private StudentService studentService;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @SentinelResource(value = "hotSearch",blockHandler = "searchBlockHandler")
    public String search(@RequestParam(value = "q") String query) {
        System.out.println("user " + query);
        return server3Remote.query(query);
    }

    public String searchBlockHandler(Throwable throwable) {
        System.out.println("阻塞了。。。");
        return throwable.getMessage();
    }
    /**
     * 关联模式
     *
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public String update() throws InterruptedException {
        System.out.println("user update");
        TimeUnit.MILLISECONDS.sleep(1_000);
        return "update user";
    }

    /***************链路模式 start ******************/
    @RequestMapping(value = "/querystu", method = RequestMethod.GET)
    public String querystu() {
        return studentService.getStudentName("querystu");
    }

    @RequestMapping(value = "/savestu", method = RequestMethod.GET)
    public String savestu() {
        return studentService.getStudentName("savestu");
    }

    /***************链路模式 end ******************/

    /**
     * 热点流控模式
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getUserById")
    @SentinelResource(value ="hotUserId")
    public String getStudentNameById(@RequestParam("id") Long id) {
        System.out.println("id: " + id);
        return "student name is " + id;
    }

}
