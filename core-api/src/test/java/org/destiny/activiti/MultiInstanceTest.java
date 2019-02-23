package org.destiny.activiti;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/24 23:46
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
public class MultiInstanceTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void testDeploy() {
        activitiRule.getRepositoryService().createDeployment()
                .addClasspathResource("org/destiny/activiti/my-process-multi.bpmn20.xml")
                .deploy();

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("userList", Arrays.asList("wk1", "wk2", "wk3"));
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", variables);
        log.info("processInstance: {}", ToStringBuilder.reflectionToString(processInstance, ToStringStyle.JSON_STYLE));
    }

    @Test
    public void testTaskList() {
        String procInstId = "4";
        List<Task> taskList = activitiRule.getTaskService().createTaskQuery().processInstanceId(procInstId).list();
        for (Task task : taskList) {
            log.info("task: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        }

    }

    @Test
    public void testComplete() {
        String taskId = "29";
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("pass", true);
        activitiRule.getTaskService().complete(taskId, variables);
    }

    @Test
    public void testHisList() {
        List<HistoricTaskInstance> historicTaskInstanceList = activitiRule.getHistoryService().createHistoricTaskInstanceQuery()
                .processInstanceId("4")
                .list();
        for (HistoricTaskInstance task : historicTaskInstanceList) {
            log.info("task: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        }
    }
}
