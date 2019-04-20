package org.destiny.activiti;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2019-04-17 12:10
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2019
 */
@Slf4j
public class ListConditionTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void testDeploy() {
        activitiRule.getRepositoryService().createDeployment()
                .addClasspathResource("org/destiny/activiti/list-condition.bpmn20.xml")
                .deploy();

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("list", Arrays.asList("wk1", "wk2", "wk3"));
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("list-condition", variables);
        log.info("processInstance: {}", processInstance.getId());
    }

    @Test
    public void testComplete() {
        activitiRule.getTaskService().complete("165011");
    }
}
