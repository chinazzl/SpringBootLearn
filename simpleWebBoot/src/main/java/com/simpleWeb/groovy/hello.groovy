import com.simpleWeb.groovy.MyGroovyClassLoader
import groovy.xml.MarkupBuilder


class Example {
    static void main(String[] args) {
        println("hello world");
        MyGroovyClassLoader loader = new MyGroovyClassLoader();
        Object o = loader.parseScript("return 'hello world from script'");
        println(o);
    }



}
