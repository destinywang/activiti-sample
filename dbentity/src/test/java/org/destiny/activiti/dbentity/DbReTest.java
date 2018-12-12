package org.destiny.activiti.dbentity;

import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/11 23:07
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class DbReTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

    @Test
    public void testDeploy() {
        activitiRule.getRepositoryService()
                .createDeployment()
                .name("二次审批流程")
                .addClasspathResource("org/destiny/activiti/SecondApprove.bpmn20.xml")
                .deploy();

    }

    @Test
    public void testSuspend() {
        activitiRule.getRepositoryService().suspendProcessDefinitionById("SecondApprove:2:2504");
//        activitiRule.getRepositoryService().isProcessDefinitionSuspended()
    }
}