package org.destiny.activiti.bpmn20;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
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

    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process-parallel.bpmn20.xml"})
    public void testParallelGateway() {
        activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

        List<Task> taskList = activitiRule.getTaskService().createTaskQuery().list();
        log.info("过并行网关时的 task 数量: {}", taskList.size());
        for (Task task : taskList) {
            log.info("task name: {}", task.getName());
        }
        assertEquals(2, taskList.size());

        activitiRule.getTaskService().complete(taskList.get(0).getId());
        List<Task> taskList1 = activitiRule.getTaskService().createTaskQuery().list();
        log.info("提交第 1 个任务时的 task 数量: [{}]", taskList1.size());
        for (Task task : taskList1) {
            log.info("task name: {}", task.getName());
        }
        assertEquals(1, taskList1.size());

        activitiRule.getTaskService().complete(taskList.get(1).getId());
        List<Task> taskList2 = activitiRule.getTaskService().createTaskQuery().list();
        log.info("提交第 2 个任务时的 task 数量: [{}]", taskList2.size());
        for (Task task : taskList2) {
            log.info("task name: {}", task.getName());
        }
        assertEquals(1, taskList1.size());
    }
}
