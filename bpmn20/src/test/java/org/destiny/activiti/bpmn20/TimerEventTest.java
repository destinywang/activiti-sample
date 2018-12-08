package org.destiny.activiti.bpmn20;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/8 10:30
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
public class TimerEventTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    /**
     * 超时边界事件
     * 流程启动之后, 流转到 commonTask, 此时让当前线程休眠 10 秒(5秒就会超时)
     * 定时任务超时后执行 timeoutTask
     */
    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process-time-boundary.bpmn20.xml"})
    public void testTimerBoundary() throws InterruptedException {
        activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        // task 列表
        List<Task> taskList = activitiRule.getTaskService().createTaskQuery().list();
        log.info("task 总数: {}", taskList.size());
        for (Task task : taskList) {
            log.info("task.name = {}", task.getName());
        }
        // 强制睡眠等待边界事件触发
        Thread.sleep(10 * 1000);

        // task 列表
        taskList = activitiRule.getTaskService().createTaskQuery().list();
        log.info("休眠后 task 总数: {}", taskList.size());
        for (Task task : taskList) {
            log.info("休眠后 task.name = {}", task.getName());
        }
    }

}
