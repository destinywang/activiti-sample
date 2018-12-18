package org.destiny.activiti;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2018-12-17 14:33
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2018
 */
@Slf4j
public class RejectTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process-reject.bpmn20.xml"})
    public void test() {
        activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        Map<String, Object> map = new HashMap<>();
        map.put("type", "N");

        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        log.info("task: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));

        activitiRule.getTaskService().complete(task.getId(), map);

        HistoricProcessInstance historicProcessInstance = activitiRule.getHistoryService().createHistoricProcessInstanceQuery()
                .processDefinitionKey("my-process")
                .singleResult();

        log.info("historicProcessInstance: {}", ToStringBuilder.reflectionToString(historicProcessInstance, ToStringStyle.JSON_STYLE));

        List<HistoricTaskInstance> historicTaskInstanceList = activitiRule.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .taskId("pass")
                .list();

        log.info("historicTaskInstanceList: {}", historicTaskInstanceList.size());

        for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
            log.info("historicTaskInstance: {}", ToStringBuilder.reflectionToString(historicTaskInstance, ToStringStyle.JSON_STYLE));
        }

    }
}
