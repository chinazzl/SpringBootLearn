package org.basic.controller;

import org.basic.config.annotation.EncryptionAnnotation;
import org.basic.entity.Result;
import org.basic.entity.ResultBuilder;
import org.basic.entity.Teacher;
import org.basic.remote.GitHubSearchRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/5 15:15
 * @Description:
 */
@RestController
@RequestMapping("/v1")
public class IndexController implements ResultBuilder {

    @Autowired
    private GitHubSearchRemote gitHubSearchRemote;

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    // @EncryptionAnnotation
    public ResponseEntity<Result<?>> getTeacher(@Validated @RequestBody Teacher teacher) {
        return success(teacher);
    }

    @RequestMapping("/github/search")
    public ResponseEntity<Result<?>> getGithubSearch(String q) {
        return success(gitHubSearchRemote.repositories(q));
    }

}
