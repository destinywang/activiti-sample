package org.destiny.activiti.listener;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/12 21:55
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class ListenerTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    /**
     * 启动/结束监听器
     *
     * eventName: [start]
     * eventName: [take]
     * eventName: [end]
     */
    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process-flow-listener.bpmn20.xml"})
    public void testEventListener() {
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        activitiRule.getTaskService().complete(task.getId());
    }


    /**
     * 节点监听器
     * eventName: [start]
     * eventName: [take]
     * delegateTask name: [assignment]
     * delegateTask name: [create]
     * delegateTask name: [complete]
     * delegateTask name: [delete]
     * eventName: [end]
     */
    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process-task-listener.bpmn20.xml"})
    public void testTaskListener() {
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        activitiRule.getTaskService().complete(task.getId());
    }
}
