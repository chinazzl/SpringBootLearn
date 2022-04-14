package org.drools.demo.service;

import org.drools.demo.Entity.vo.StudentVO;
import org.drools.demo.dao.IStuDao;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Julyan
 * @version V1.0
 * @Description:
 * @Date: 2022/3/25 14:45
 */
@Service("ruleService")
public class RuleService {

    @Autowired
    private KieBase kieBase;
    @Autowired
    private IStuDao stuDao;

    public List<StudentVO> rule(String param) {
        List<StudentVO> studentVos = stuDao.findAll();
        System.out.println(studentVos.size());
        String rule = "import org.drools.demo.Entity.vo.StudentVO;\n" +
                "   import java.util.List;\n" +
                "query \"queryStudentByAge\"\n" +
                "   $l:List(size()>0);\n" +
                "   $stu:StudentVO(%s) from $l\n" +
                "end";
        String newRule = String.format(rule, param);
        System.out.println(newRule);
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(newRule, ResourceType.DRL);
        KieBase build = kieHelper.build();
        KieSession kieSession = build.newKieSession();
        kieSession.insert(studentVos);
        QueryResults queryStudentByAge = kieSession.getQueryResults("queryStudentByAge");
        List<StudentVO> result = new ArrayList<>();
        for (QueryResultsRow queryResultsRow : queryStudentByAge) {
            // 参数是实则知LHS的实体别名，当前的别名是$s @link{advancedQuery.drl}
            StudentVO student = (StudentVO) queryResultsRow.get("$stu");
            result.add(student);
            System.out.println("匹配到有参的query Fact " + student);
        }
        kieSession.dispose();
        return result;
    }
}
