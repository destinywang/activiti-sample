package org.destiny.activiti.coreapi;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.*;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Slf4j
public class TaskServiceTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process-task.bpmn20.xml"})
    public void testTaskService() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("message", "my test message");
        TaskService taskService = activitiRule.getTaskService();
        // 部署流程定义文件
        ProcessInstance processInstance = activitiRule.getRuntimeService()
                .startProcessInstanceByKey("my-process", variables);
        Task task = taskService.createTaskQuery().singleResult();
        log.info("task: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        log.info("task.description: {}", task.getDescription());

        // 设置变量
        taskService.setVariable(task.getId(), "k1", "v1");
        taskService.setVariableLocal(task.getId(), "localK1", "localV1");

        // local 只在 task 范围可见
        Map<String, Object> taskServiceVariables = taskService.getVariables(task.getId());
        Map<String, Object> taskServiceVariablesLocal = taskService.getVariablesLocal(task.getId());
        // 根据流程获取
        Map<String, Object> processVariables = activitiRule.getRuntimeService().getVariables(task.getExecutionId());

        log.info("taskServiceVariables: {}", taskServiceVariables);             // {k1=v1, localK1=localV1, message=my test message}
        log.info("taskServiceVariablesLocal: {}", taskServiceVariablesLocal);   // {localK1=localV1}
        log.info("processVariables: {}", processVariables);                     // {k1=v1, message=my test message}

        Map<String, Object> completeVar = Maps.newHashMap();
        completeVar.put("cKey1", "cValue1");
        taskService.complete(task.getId(), completeVar);

        Task task1 = taskService.createTaskQuery().taskId(task.getId()).singleResult();
        log.info("task1: {}", task1);
    }

    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process-task.bpmn20.xml"})
    public void testTaskServiceUser() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("message", "my test message");
        activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", variables);
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        log.info("task: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        log.info("task.description: {}", task.getDescription());

        taskService.setOwner(task.getId(), "user1");
        // 可能存在覆盖已有代办放的问题, 因此不推荐
        // taskService.setAssignee(task.getId(), "destiny");

        // 查询在候选人列表, 且未指定办理人的 task 列表
        List<Task> taskList = taskService.createTaskQuery()
                .taskCandidateOrAssigned("destiny")
                .taskUnassigned()
                .listPage(0, 100);

        // 使用 claim 设置候选人
        for (Task task1 : taskList) {
            try {
                taskService.claim(task1.getId(), "destiny");
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }

        // 查看当前 task 的所有用户关系内容
        List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(task.getId());
        for (IdentityLink identityLink : identityLinkList) {
            log.info("identityLink: {}", identityLink);
        }

        // 完成任务, 首先找到处于代办状态的所有 task
        List<Task> destinys = taskService.createTaskQuery().taskAssignee("destiny").listPage(0, 100);
        for (Task destiny : destinys) {
            variables.clear();
            variables.put("cKey1", "cValue1");
            taskService.complete(destiny.getId(), variables);
        }

        destinys = taskService.createTaskQuery().taskAssignee("destiny").listPage(0, 100);
        log.info("是否存在: {}", !CollectionUtils.isEmpty(destinys));
    }


    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process-task.bpmn20.xml"})
    public void testTaskAttachment() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("message", "my test message");
        activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", variables);
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        // 可以上传数据流或 url
        Attachment attachment = taskService.createAttachment("url", task.getId(), task.getProcessInstanceId(), "name", "desc", "/url/test.png");
        log.info("attachment: {}", attachment);
        List<Attachment> taskAttachments = taskService.getTaskAttachments(task.getId());
        for (Attachment taskAttachment : taskAttachments) {
            log.info("taskAttachment: {}", ToStringBuilder.reflectionToString(taskAttachment, ToStringStyle.JSON_STYLE));
        }
    }

    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process-task.bpmn20.xml"})
    public void testTaskComment() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("message", "my test message");
        activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", variables);
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        // 添加评论
        taskService.addComment(task.getId(), task.getProcessInstanceId(), "record note1");
        taskService.addComment(task.getId(), task.getProcessInstanceId(), "record note2");
        taskService.setOwner(task.getId(), "destiny");
        taskService.setAssignee(task.getId(), "destiny");

        List<Comment> taskComments = taskService.getTaskComments(task.getId());
        for (Comment taskComment : taskComments) {
            log.info("taskComment: {}", ToStringBuilder.reflectionToString(taskComment, ToStringStyle.JSON_STYLE));
        }

        // 事件记录
        List<Event> taskEvents = taskService.getTaskEvents(task.getId());
        for (Event taskEvent : taskEvents) {
            log.info("taskEvent: {}", ToStringBuilder.reflectionToString(taskEvent, ToStringStyle.JSON_STYLE));
        }

    }

    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process-task.bpmn20.xml"})
    public void testDelegate() {

    }
}
