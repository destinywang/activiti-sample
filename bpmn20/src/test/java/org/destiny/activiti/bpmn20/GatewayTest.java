package org.destiny.activiti.bpmn20;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/8 17:56
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
public class GatewayTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process-exclusive.bpmn20.xml"})
    public void testExclusiveGateway() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("key1", 3);
        variables.put("score", 1);

        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", variables);

        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();

        // org.destiny.activiti.bpmn20.GatewayTest  - task.name = 精英
        log.info("task.name = {}", task.getName());
//        assertEquals(task.getName(), "精英");
    }

}
