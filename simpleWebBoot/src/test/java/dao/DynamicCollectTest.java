package dao;

import com.simpleWeb.Runner;
import com.simpleWeb.entity.request.PageQueryRequest;
import com.simpleWeb.entity.vo.PageResult;
import com.simpleWeb.service.UniversalDataCollectionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author: zhaolin
 * @Date: 2025/8/16
 * @Description:
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Runner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public class DynamicCollectTest {

    @Resource
    private UniversalDataCollectionService universalDataCollectionService;

    @Test
    public void collectAllTableDataWithPage() throws InterruptedException {
        PageQueryRequest request = new PageQueryRequest();
        PageResult<List<Map<String, Object>>> listPageResult = universalDataCollectionService.collectDatas(request);
        log.info("listPageResult={}", listPageResult);
        Thread.currentThread().join();

    }

}
