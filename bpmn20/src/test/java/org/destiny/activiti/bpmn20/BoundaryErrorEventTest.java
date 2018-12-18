package org.destiny.activiti.bpmn20;

import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/8 13:21
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class BoundaryErrorEventTest extends PluggableActivitiTestCase {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // 设置当前用户
        Authentication.setAuthenticatedUserId("destiny");
    }

    @Override
    public void tearDown() throws Exception {
        Authentication.setAuthenticatedUserId(null);
        super.tearDown();
    }

    @Deployment(resources = {"org/destiny/activiti/reviewSalesLead.bpmn20.xml"})
    public void testReviewSalesLeadProcess() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("details", "interesting");
        variables.put("customerName", "Camery");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("reviewSaledLead", variables);
        log.info("processInstance: {}", ToStringBuilder.reflectionToString(processInstance, ToStringStyle.JSON_STYLE));

        Task task = taskService.createTaskQuery()
                .taskAssignee("destiny")
                .singleResult();

        log.info("task: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        // 使用断言确认
        assertEquals(task.getName(), "Provide new sales lead");

        // 提交节点
        taskService.complete(task.getId());

        // 进入并行网关, 会同时生成两个 task
        Task ratingTask = taskService.createTaskQuery().taskCandidateGroup("accountancy").singleResult();
        log.info("ratingTask: {}", ToStringBuilder.reflectionToString(ratingTask, ToStringStyle.JSON_STYLE));
        assertEquals(ratingTask.getName(), "Review customer rating");

        Task profitabilityTask = taskService.createTaskQuery().taskCandidateGroup("management").singleResult();
        log.info("profitabilityTask: {}", ToStringBuilder.reflectionToString(profitabilityTask, ToStringStyle.JSON_STYLE));
        assertEquals(profitabilityTask.getName(), "Review profitability");

        // Review profitability 提交后就会触发 errorEvent
        variables = new HashMap<>();
        variables.put("notEnoughInformation", true);
        taskService.complete(profitabilityTask.getId(), variables);

        // 查找流程发起者 destiny 对应的 task
        // 此时 errorEvent 会被边界条件捕获, 流转到 Review profitability
        Task provideDetailsTask = taskService.createTaskQuery().taskAssignee("destiny").singleResult();
        log.info("provideDetailsTask: {}", ToStringBuilder.reflectionToString(provideDetailsTask, ToStringStyle.JSON_STYLE));
        assertEquals(provideDetailsTask.getName(), "Provide additional details");

        // 完成 Review profitability 节点后, 会重新进入子流程
        taskService.complete(provideDetailsTask.getId());
        List<Task> reviewTasks = taskService.createTaskQuery().orderByTaskName().asc().list();
        for (Task reviewTask : reviewTasks) {
            log.info("reviewTask: {}", ToStringBuilder.reflectionToString(reviewTask, ToStringStyle.JSON_STYLE));
        }
        assertEquals(reviewTasks.get(0).getName(), "Review customer rating");
        assertEquals(reviewTasks.get(1).getName(), "Review profitability");

        taskService.complete(reviewTasks.get(0).getId());
        variables.put("notEnoughInformation", false);
        taskService.complete(reviewTasks.get(1).getId(), variables);
        assertProcessEnded(processInstance.getId());
    }

    @Deployment(resources = {"org/destiny/activiti/reviewSalesLead.bpmn20.xml"})
    public void testParallerGateway() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("details", "interesting");
        variables.put("customerName", "Camery");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("reviewSaledLead", variables);
        log.info("processInstance: {}", ToStringBuilder.reflectionToString(processInstance, ToStringStyle.JSON_STYLE));

        Task task = taskService.createTaskQuery()
                .taskAssignee("destiny")
                .singleResult();

        log.info("task: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        // 使用断言确认
        assertEquals(task.getName(), "Provide new sales lead");

        // 提交节点
        taskService.complete(task.getId());

        // 进入并行网关, 会同时生成两个 task
        Task ratingTask = taskService.createTaskQuery().taskCandidateGroup("accountancy").singleResult();
        log.info("ratingTask: {}", ToStringBuilder.reflectionToString(ratingTask, ToStringStyle.JSON_STYLE));
        assertEquals(ratingTask.getName(), "Review customer rating");

        Task profitabilityTask = taskService.createTaskQuery().taskCandidateGroup("management").singleResult();
        log.info("profitabilityTask: {}", ToStringBuilder.reflectionToString(profitabilityTask, ToStringStyle.JSON_STYLE));
        assertEquals(profitabilityTask.getName(), "Review profitability");

        // 提交 ratingTask
        taskService.complete(ratingTask.getId());

        List<Task> taskList = taskService.createTaskQuery().list();
        log.info("task 总数: {}", taskList);
        for (Task task1 : taskList) {
            log.info("task1: {}", task1.getName());
        }


        assertProcessEnded(processInstance.getId());
    }
}
