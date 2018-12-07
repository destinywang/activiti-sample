package org.destiny.activiti.coreapi;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;


@Slf4j
public class CoreTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    private ProcessInstance processInstance;

    private String parseJson(Object object) {
        return ToStringBuilder.reflectionToString(object, ToStringStyle.JSON_STYLE);
    }

    @Before
    public void setUp() {
        Deployment deployment = activitiRule.getRepositoryService().createDeployment()
                .addClasspathResource("org/destiny/activiti/my-process-task.bpmn20.xml")
                .deploy();
        log.info("deployment: {}", parseJson(deployment));
        // 启动流程
        processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        log.info("processInstance: {}", parseJson(processInstance));
        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        log.info("task: {}", parseJson(task));
    }

    @Test
    public void passProcess() {
        TaskService taskService = activitiRule.getTaskService();
        String candidate = "destiny";
        List<Task> taskList = taskService.createTaskQuery()
                .taskCandidateUser(candidate)
                .taskUnassigned()
                .list();
        log.info("查询到的 task 数量: {}", taskList.size());
        for (Task task : taskList) {
            log.info("当前可执行的 task: {}", parseJson(task));
            taskService.claim(task.getId(), "destiny");
            log.info("claim 之后的 task: {}", parseJson(task));
            taskService.complete(task.getId());
        }

        taskList = taskService.createTaskQuery()
                .taskCandidateUser(candidate)
                .taskUnassigned()
                .list();
        for (Task task : taskList) {
            log.info("完成通过后还剩的 task: {}", parseJson(task));
            taskService.complete(task.getId());
        }

        List<Task> taskList1 = activitiRule.getTaskService().createTaskQuery().taskAssignee("destiny").list();
        log.info("办理人为 destiny 的 task 数量: {}", taskList1.size());
        for (Task task : taskList1) {
            log.info("办理人为 destiny 的 task: {}", parseJson(task));
        }
    }

    /**
     * 直接提交
     */
    @Test
    public void commitProcess() {

    }

    @Test
    public void joinProcess() {

    }
}
