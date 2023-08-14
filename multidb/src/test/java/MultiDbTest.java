import org.junit.Test;
import org.junit.runner.RunWith;
import org.multidb.Example;
import org.multidb.dao.cluster.TeacherDao;
import org.multidb.dao.master.StuDao;
import org.multidb.entity.Student;
import org.multidb.entity.Teacher;
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

    @Autowired
    private TeacherDao teacherDao;
    @Test
    public void stuTest(){
        Student student = stuDao.findStuById(1);
        System.out.println(student.getSage());
    }

    @Test
    public void teacherTest(){
        Teacher teacher = teacherDao.findTeacherById(2);
        System.out.println(teacher.getImage().length);
    }
}
