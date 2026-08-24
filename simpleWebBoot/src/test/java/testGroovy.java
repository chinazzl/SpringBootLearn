import com.simpleWeb.groovy.MyGroovyClassLoader;
import org.junit.jupiter.api.Test;

/**
 * @author: zhaolin
 * @Date: 2025/3/18
 * @Description:
 **/
public class testGroovy {

    @Test
    public void test() {
        MyGroovyClassLoader loader = new MyGroovyClassLoader();
        Object o = loader.parseScript("hello world");
    }
}
