package dao;

import com.simpleWeb.Runner;
import com.simpleWeb.entity.db.cmp.CmpResourceDO;
import com.simpleWeb.mapper.cmp.CmpResourceMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.sql.DataSourceDefinition;
import java.util.List;

/**
 * @author: zhaolin
 * @Date: 2025/7/17
 * @Description:
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Runner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DaoTest {

    @Autowired
    private CmpResourceMapper cmpResourceMapper;

    @Test
    public void testDao() {
        List<CmpResourceDO> cmpResourceDOS = cmpResourceMapper.queryVmwareResourceDOList();
        System.out.println(cmpResourceDOS);
    }
}
