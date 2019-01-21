package org.destiny.activiti.coreapi;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
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
public class ConditionTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    private String parseJson(Object object) {
        return ToStringBuilder.reflectionToString(object, ToStringStyle.JSON_STYLE);
    }

    @Test
    public void deploy() {
        Deployment deploy = activitiRule.getRepositoryService().createDeployment()
                .addClasspathResource("org/destiny/activiti/my-process-condition.bpmn20.xml")
                .deploy();
        Map<String, Object> map = Maps.newHashMap();
        map.put("days", 2);
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", map);
        log.info("processInstanceId: {}", processInstance.getId());
    }

}
