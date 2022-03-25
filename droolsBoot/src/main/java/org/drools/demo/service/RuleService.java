package org.drools.demo.service;

/**
 * @author Julyan
 * @version V1.0
 * @Description:
 * @Date: 2022/3/25 14:45
 */

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ruleService")
public class RuleService {

    @Autowired
    private KieBase kieBase;

    public void rule() {
        System.out.println("扫描到包 共 " + kieBase.getKiePackages().size());
        KieSession kieSession = kieBase.newKieSession();
        kieSession.fireAllRules();
        kieSession.dispose();

    }
}
