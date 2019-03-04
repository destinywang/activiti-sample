package org.destiny.activiti.jump;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.destiny.activiti.cmd.JumpCmd;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2019-03-04 10:50
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2019
 */
@Slf4j
public class ClientTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void deploy() {
        Deployment deploy = activitiRule.getRepositoryService().createDeployment()
                .addClasspathResource("org/destiny/activiti/my-process-condition.bpmn20.xml")
                .key("my-process")
                .deploy();

        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        log.info("processInstanceId: {}", processInstance.getId());
    }

    @Test
    public void complete() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("condition", 1);
        activitiRule.getTaskService().complete("8", map);
    }

    @Test
    public void jump() {
        activitiRule.getManagementService().executeCommand(new JumpCmd("2503", "userTask1"));
    }
}
