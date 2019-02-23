package org.destiny.activiti.coreapi;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2019-01-21 11:22
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2019
 */
@Slf4j
public class ServiceTaskTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    private String parseJson(Object object) {
        return ToStringBuilder.reflectionToString(object, ToStringStyle.JSON_STYLE);
    }

    @Test
    public void deploy() {
        Deployment deploy = activitiRule.getRepositoryService().createDeployment()
                .addClasspathResource("org/destiny/activiti/my-process-service-task.bpmn20.xml")
                .deploy();

        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        log.info("processInstanceId: {}", processInstance.getId());
    }

    @Test
    public void query() {
        HistoricActivityInstance serviceTask = activitiRule.getHistoryService().createHistoricActivityInstanceQuery()
                .activityType("serviceTask")
                .singleResult();

        log.info("serviceTask: {}", serviceTask);
    }
}
