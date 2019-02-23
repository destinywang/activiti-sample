package org.destiny.activiti;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2019/2/22 22:37
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
public class RepositoryTest {

    volatile

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void test() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Deployment deployment = processEngine.getRepositoryService()
                .createDeployment()
                .name("my-process")
                .addClasspathResource("process/my-process.bpmn20.xml")
                .deploy();

        log.info("deployment: {}", deployment);
    }
}
