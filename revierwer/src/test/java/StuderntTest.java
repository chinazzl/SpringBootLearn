import org.iocode.service.StudentService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Felix on 2017/7/27.
 */
public class StuderntTest {

    @Test
    public void Stu(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application.xml");
        StudentService studentService = (StudentService) applicationContext.getBean("studentService");
        studentService.hello("11");


    }
}
