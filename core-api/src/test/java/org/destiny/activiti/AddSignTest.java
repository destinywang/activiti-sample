package org.destiny.activiti;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.destiny.activiti.addsign.AddNode;
import org.destiny.activiti.util.GenerateActivityUtils;
import org.destiny.activiti.util.TaskModel;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/22 23:46
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
public class AddSignTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void testDeploy() {
        Deployment deployment = activitiRule.getRepositoryService().createDeployment()
                .addClasspathResource("org/destiny/activiti/my-process-add-sign.bpmn20.xml")
                .deploy();
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        log.info("processInstance: {}", ToStringBuilder.reflectionToString(processInstance, ToStringStyle.JSON_STYLE));
        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        log.info("task: {}",  ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
    }

    @Test
    public void testAddSign() {
        String taskId = "8";
        TaskEntity taskEntity = (TaskEntity) activitiRule.getTaskService().createTaskQuery()
                .taskId(taskId)
                .singleResult();

        String firstNodeId = "destiny-a";
        String lastNodeId = "destiny-b";

        List<TaskModel> taskModelList = Lists.newArrayList();
        TaskModel taskModel1 = GenerateActivityUtils.generateTaskModel("destiny-d", "destiny-d", "destiny-d");
        TaskModel taskModel2 = GenerateActivityUtils.generateTaskModel("destiny-e", "destiny-e", "destiny-e");

        taskModelList.add(taskModel1);
        taskModelList.add(taskModel2);

        AddNode addNode = new AddNode();
        addNode.addUserTask(taskEntity.getProcessDefinitionId(), taskEntity.getProcessInstanceId(),
                activitiRule.getProcessEngine(), taskModelList, firstNodeId, lastNodeId, true,
                true, taskId, taskModelList.get(0).getId());
    }

    @Test
    public void testComplete() {
        String taskId = "2502";
        activitiRule.getTaskService().complete(taskId);
        for (Task task : activitiRule.getTaskService().createTaskQuery().list()) {
            log.info("task: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        }
    }
}
