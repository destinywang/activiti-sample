package org.destiny.activiti.shareniu;

import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.destiny.activiti.cmd.GetProcessCmd;
import org.destiny.activiti.cmd.GetProcessDefinitionCacheEntryCmd;
import org.destiny.activiti.cmd.JumpCmd;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/22 14:00
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
public class CountSignTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
//    @Deployment(resources = {"org/destiny/activiti/my-process-add-sign.bpmn20.xml"})
    public void testDeploy() {
        activitiRule.getRepositoryService().createDeployment()
                .addClasspathResource("org/destiny/activiti/my-process-add-sign.bpmn20.xml")
                .deploy();
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        log.info("processInstance: {}", ToStringBuilder.reflectionToString(processInstance, ToStringStyle.JSON_STYLE));
        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        log.info("task: {}",  ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
    }

    @Test
    public void testJump() {
        activitiRule.getManagementService().executeCommand(new JumpCmd("8", "destinyC"));
        List<Task> list = activitiRule.getTaskService().createTaskQuery().list();
        for (Task task : list) {
            log.info("task: {}",  ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        }

    }

    @Test
    public void testComplete() {
        activitiRule.getTaskService().complete("2502");
    }

    /**
     * 添加一个任务节点
     */
    @Test
    public void testAddTask() {
        String taskId = "8";
        // 获取当前任务
        TaskEntity taskEntity = (TaskEntity) activitiRule.getTaskService().createTaskQuery()
                .taskId(taskId)
                .singleResult();

        log.info("taskEntity: {}", taskEntity);

        String processDefinitionId = taskEntity.getProcessDefinitionId();
        Process process = activitiRule.getManagementService().executeCommand(new GetProcessCmd(processDefinitionId));
        log.info("process: {}", ToStringBuilder.reflectionToString(process, ToStringStyle.JSON_STYLE));

        UserTask userTask = new UserTask();
        userTask.setId("d");
        userTask.setName("destinyD");
        userTask.setAssignee("destiny-d");
        userTask.setBehavior(createUserTaskBehavior(userTask));

        //
        String targetActivityId = "destiny-b";
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId("destiny-s1");
        sequenceFlow.setName("新增连线");
        // 节点连线绑定
        userTask.setOutgoingFlows(Arrays.asList(sequenceFlow));
        sequenceFlow.setTargetFlowElement(process.getFlowElement(targetActivityId));
        sequenceFlow.setTargetRef(targetActivityId);

        process.addFlowElement(userTask);
        process.addFlowElement(sequenceFlow);

        // 更新缓存
//        ProcessDefinitionCacheEntry oldProcessDefinitionCacheEntry = activitiRule.getManagementService().executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId));
//        log.info("oldProcessDefinitionCacheEntry getProcess: {}", ToStringBuilder.reflectionToString(oldProcessDefinitionCacheEntry.getProcess(), ToStringStyle.JSON_STYLE));
//
//        oldProcessDefinitionCacheEntry.setProcess(process);
//
//        ProcessDefinitionCacheEntry newProcessDefinitionCacheEntry = activitiRule.getManagementService().executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId));
//        log.info("newProcessDefinitionCacheEntry getProcess: {}", ToStringBuilder.reflectionToString(newProcessDefinitionCacheEntry.getProcess(), ToStringStyle.JSON_STYLE));

        // 执行自由跳转命令
        activitiRule.getManagementService().executeCommand(new JumpCmd(taskId, userTask.getId()));
    }

    private UserTaskActivityBehavior createUserTaskBehavior(UserTask userTask) {
        // 通过流程引擎配置类获取工厂类
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) activitiRule.getProcessEngine().getProcessEngineConfiguration();
        ActivityBehaviorFactory activityBehaviorFactory = processEngineConfiguration.getActivityBehaviorFactory();
        return activityBehaviorFactory.createUserTaskActivityBehavior(userTask);
    }
}
