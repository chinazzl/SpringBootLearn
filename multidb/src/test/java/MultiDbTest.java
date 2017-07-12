import org.junit.Test;
import org.junit.runner.RunWith;
import org.multidb.Example;
import org.multidb.dao.master.StuDao;
import org.multidb.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Felix on 2017/7/12.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Example.class)

public class MultiDbTest {

    @Autowired
    private StuDao stuDao;
    @Test
    public void stuTest(){
        Student student = stuDao.findStuById(1);
        System.out.println(student.getSage());
    }
}
