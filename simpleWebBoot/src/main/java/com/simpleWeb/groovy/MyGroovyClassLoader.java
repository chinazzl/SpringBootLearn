package com.simpleWeb.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

import java.lang.reflect.InvocationTargetException;

/**
 * @author: zhaolin
 * @Date: 2025/3/18
 * @Description:
 **/
public class MyGroovyClassLoader {

    private final GroovyClassLoader groovyClassLoader;

    public MyGroovyClassLoader() {
        this.groovyClassLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
    }

    public Object parseScript(String scriptText) {
        Object result = null;
        try {
            Script script = (Script) groovyClassLoader.parseClass(scriptText).getDeclaredConstructor().newInstance();
            result = script.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
