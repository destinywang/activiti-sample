package org.destiny.activiti.bd;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2018-12-12 11:44
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2018
 */
@Slf4j
public class DbRuTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void testRuntime() {
        Deployment deployment = activitiRule.getRepositoryService()
                .createDeployment()
                .name("二次审批")
                .addClasspathResource("org/destiny/activiti/SecondApprove.bpmn20.xml")
                .deploy();
        log.info("deployment: {}", deployment);

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("k1", "v1");
        ProcessInstance processInstance = activitiRule.getRuntimeService()
                .startProcessInstanceByKey("SecondApprove", variables);
    }
}
