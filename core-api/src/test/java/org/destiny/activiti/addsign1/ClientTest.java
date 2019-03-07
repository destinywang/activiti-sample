package org.destiny.activiti.addsign1;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ManagementService;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.destiny.activiti.addsign1.model.AddSign;
import org.destiny.activiti.addsign1.model.TaskModel;
import org.destiny.activiti.addsign1.util.ActivityUtils;
import org.destiny.activiti.cmd.GetProcessCmd;
import org.destiny.activiti.cmd.GetProcessDefinitionCacheEntryCmd;
import org.destiny.activiti.cmd.JumpCmd;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2019-03-04 17:14
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
                .addClasspathResource("org/destiny/activiti/my-process-add-sign.bpmn20.xml")
                .key("my-process")
                .deploy();

        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        log.info("processInstanceId: {}", processInstance.getId());
    }

    @Test
    public void complete() {
//        Map<String, Object> map = Maps.newHashMap();
//        map.put("condition", 1);
        activitiRule.getTaskService().complete("20002");
    }

    public void testAddOneTask(String taskId, String targetActivityId) {
        // 获取当前的任务
        TaskEntity taskEntity = (TaskEntity) activitiRule.getTaskService().createTaskQuery().taskId(taskId).singleResult();
        log.info("taskEntity: {}", taskEntity);
        String processDefinitionId = taskEntity.getProcessDefinitionId();
        ManagementService managementService = activitiRule.getManagementService();
        Process process = managementService.executeCommand(new GetProcessCmd(processDefinitionId));
        log.info("process: {}", process);

        // 创建新节点
        UserTask userTask = new UserTask();
        userTask.setId("destinyD");
        userTask.setName("加签节点 destinyD");
        userTask.setAssignee("destiny-d");
        userTask.setBehavior(createUserTaskBehavior(userTask));

        // 新节点的目标连线
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId("extra");
        userTask.setOutgoingFlows(Arrays.asList(sequenceFlow));
        sequenceFlow.setTargetFlowElement(process.getFlowElement(targetActivityId));
        sequenceFlow.setTargetRef(targetActivityId);

        process.addFlowElement(userTask);
        process.addFlowElement(sequenceFlow);

        // 更新缓存
        ProcessDefinitionCacheEntry processDefinitionCacheEntry = managementService.executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId));
        processDefinitionCacheEntry.setProcess(process);
        Process processCache = managementService.executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId)).getProcess();

        log.info("processCache: {}", processCache);

        // 跳转
        managementService.executeCommand(new JumpCmd(taskId, userTask.getId()));
    }

    private UserTaskActivityBehavior createUserTaskBehavior(UserTask userTask) {
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) activitiRule.getProcessEngine().getProcessEngineConfiguration();
        ActivityBehaviorFactory activityBehaviorFactory = processEngineConfiguration.getActivityBehaviorFactory();
        return activityBehaviorFactory.createUserTaskActivityBehavior(userTask);
    }

    @Test
    public void list() {
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) activitiRule.getProcessEngine().getProcessEngineConfiguration();
        SqlSessionFactory sqlSessionFactory = processEngineConfiguration.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        AddSignMapper mapper = sqlSession.getMapper(AddSignMapper.class);
        List<AddSign> addSigns = mapper.find(null);
        log.info("addSigns: {}", addSigns);
        sqlSession.close();
    }

    /**
     *
     */
    @Test
    public void addSignTest() {
        String taskId = "17508";
        TaskEntity taskEntity = (TaskEntity) activitiRule.getTaskService().createTaskQuery()
                .taskId(taskId)
                .singleResult();
        log.info("taskEntity: {}", taskEntity);
        String firstNodeId = "destinyA";
        String lastNodeId = "destinyB";
        List<TaskModel> taskModelList = Lists.newArrayList();

        TaskModel taskModel1 = ActivityUtils.buildTaskModel("destinyD", "destinyD", "destiny-d");
        TaskModel taskModel2 = ActivityUtils.buildTaskModel("destinyD", "destinyD", "destiny-d");

        taskModelList.add(taskModel1);
        taskModelList.add(taskModel2);

        AddSignService addSignService = new AddSignService();
        addSignService.addUserTask(taskEntity.getProcessDefinitionId(), taskEntity.getProcessInstanceId(),
                activitiRule.getProcessEngine(), taskModelList, firstNodeId, lastNodeId, true, true,
                taskEntity.getId(), taskModelList.get(0).getId());
    }
}