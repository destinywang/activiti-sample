package org.destiny.activiti.addsign1;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.apache.ibatis.session.SqlSession;
import org.destiny.activiti.addsign1.model.AddSign;
import org.destiny.activiti.addsign1.model.TaskModel;
import org.destiny.activiti.addsign1.model.TmpActivityModel;
import org.destiny.activiti.addsign1.util.ActivityUtils;
import org.destiny.activiti.cmd.GetProcessDefinitionCacheEntryCmd;
import org.destiny.activiti.cmd.JumpCmd;

import java.util.Arrays;
import java.util.List;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2019-03-04 20:47
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2019
 */
@Slf4j
public class AddSignService {

    /**
     * @param procDefId     流程定义 ID
     * @param procInstId    流程实例 ID
     * @param processEngine 流程引擎
     * @param taskModelList 加签节点列表
     * @param firstNodeId   加签开始节点 ID
     * @param lastNodeId    加签结束节点 ID
     * @param persistence   是否持久化
     * @param onset         是否需要立即跳转
     * @param taskId        taskID
     * @param targetNodeId  目标节点
     */
    public void addUserTask(String procDefId, String procInstId, ProcessEngine processEngine, List<TaskModel> taskModelList,
                            String firstNodeId, String lastNodeId, boolean persistence, boolean onset, String taskId, String targetNodeId) {
        ManagementService managementService = processEngine.getManagementService();
        ProcessDefinitionCacheEntry processDefinitionCacheEntry = managementService.executeCommand(new GetProcessDefinitionCacheEntryCmd(procDefId));
        // 通过缓存获取
        Process process = processDefinitionCacheEntry.getProcess();
        // 批量生成任务, 循环遍历 TaskModel
        List<UserTask> userTaskList = Lists.newArrayList();
        taskModelList.forEach(taskModel -> {
            UserTask userTask = ActivityUtils.convertToUserTask(taskModel, processEngine);
            userTaskList.add(userTask);
            process.addFlowElement(userTask);
        });
        // 构造并添加连线
        for (int i = 0; i < userTaskList.size(); ++i) {
            UserTask userTask = userTaskList.get(i);
            SequenceFlow sequenceFlow = null;
            if (i == userTaskList.size() - 1) {
                // 如果是最后一个节点
                sequenceFlow = ActivityUtils.buildSequenceFlow(userTask.getId() + "-->" + lastNodeId,
                        userTask.getId() + "-->" + lastNodeId, userTask.getId(), lastNodeId);
                sequenceFlow.setTargetRef(lastNodeId);
            } else {
                // 如果不是最后一个
                ActivityUtils.buildSequenceFlow(userTask.getId() + "-->" + userTaskList.get(i + 1).getId(),
                        userTask.getId() + "-->" + userTaskList.get(i + 1).getId(),
                        userTask.getId(), userTaskList.get(i + 1).getId());
                sequenceFlow.setTargetFlowElement(userTaskList.get(i + 1));
            }
            userTask.setOutgoingFlows(Arrays.asList());
            process.addFlowElement(sequenceFlow);
        }
        log.info("process: {}", process);
        // 更新缓存
        processDefinitionCacheEntry.setProcess(process);
        // 如果需要生效
        if (onset) {
            managementService.executeCommand(new JumpCmd(taskId, targetNodeId));
        }
        // 如果需要持久化
        if (persistence) {
            persistenceToDB(procDefId, procInstId, firstNodeId, lastNodeId, taskModelList, processEngine);
        }
    }

    /**
     * 将加签的任务节点添加到数据库
     * @param procDefId
     * @param procInstId
     * @param firstNodeId
     * @param lastNodeId
     * @param taskModelList
     * @param processEngine
     */
    private void persistenceToDB(String procDefId, String procInstId, String firstNodeId, String lastNodeId, List<TaskModel> taskModelList, ProcessEngine processEngine) {
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
        SqlSession sqlSession = processEngineConfiguration.getSqlSessionFactory().openSession();
        AddSignMapper mapper = sqlSession.getMapper(AddSignMapper.class);
        TmpActivityModel tmpActivityModel = new TmpActivityModel();
        tmpActivityModel.setFirstId(firstNodeId);
        tmpActivityModel.setLastId(lastNodeId);
        tmpActivityModel.setActivityList(taskModelList);
        StringBuilder stringBuilder = new StringBuilder();
        for (TaskModel taskModel : taskModelList) {
            stringBuilder.append(taskModel.getId() + ",");
        }
        tmpActivityModel.setActivityIds(stringBuilder.toString());

        AddSign addSign = new AddSign();
        addSign.setProcessDefinitionId(procDefId);
        addSign.setProcessInstanceId(procInstId);
        addSign.setPropertiesText(JSON.toJSONString(tmpActivityModel));
        addSign.setCreateTime(System.currentTimeMillis());
        mapper.insert(addSign);

        sqlSession.commit();
        sqlSession.close();
    }
}
