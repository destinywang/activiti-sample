package org.destiny.activiti.config;

import org.activiti.engine.logging.LogMDC;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/1 21:55
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class ConfigInterceptorTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti_interceptor.cfg.xml");

    @Test
    @Deployment(resources = {"my-process.bpmn20.xml"})
    public void test() {
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
//        assertNotNull(processInstance);
        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
//        assertEquals("Activiti is awesome!", task.getName());
        activitiRule.getTaskService().complete(task.getId());
    }
}
