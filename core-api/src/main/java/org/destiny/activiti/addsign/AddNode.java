package org.destiny.activiti.addsign;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ibatis.session.SqlSession;
import org.destiny.activiti.cmd.GetProcessCmd;
import org.destiny.activiti.cmd.GetProcessDefinitionCacheEntryCmd;
import org.destiny.activiti.cmd.JumpCmd;
import org.destiny.activiti.mapper.CreationMapper;
import org.destiny.activiti.util.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/22 22:38
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
public class AddNode {

    /**
     * 加签
     * @param processDefinitionId       流程定义 id
     * @param processInstanceId         流程实例 id
     * @param processEngine             引擎
     * @param taskModelList             加签的节点列表
     * @param firstNodeId               起始节点 id
     * @param lastNodeId                结束节点 id
     * @param persistenceToDB           是否持久化到数据库
     * @param fire                      是否立即执行
     * @param taskId                    任务 id
     * @param targetNodeId              目标节点 id
     */
    public void addUserTask(String processDefinitionId, String processInstanceId, ProcessEngine processEngine,
                            List<TaskModel> taskModelList, String firstNodeId, String lastNodeId,
                            boolean persistenceToDB, boolean fire, String taskId, String targetNodeId) {

//        ProcessDefinition processDefinition = null;
//        if (processDefinitionId != null) {
//            ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
//            DeploymentManager deploymentManager = processEngineConfiguration.getDeploymentManager();
//            processDefinition = deploymentManager.findDeployedProcessDefinitionById(processDefinitionId);
//            if (processDefinition == null) {
//                 throw new RuntimeException("找不到流程定义");
//            }
//        }
        
        ManagementService managementService = processEngine.getManagementService();
        managementService.executeCommand(new GetProcessCmd(processDefinitionId));

        // 获取缓存
        ProcessDefinitionCacheEntry processDefinitionCacheEntry = managementService.executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId));
        Process process = processDefinitionCacheEntry.getProcess();
        List<UserTask> userTaskList = Lists.newArrayList();
        // 批量生成任务
        for (TaskModel taskModel : taskModelList) {
            UserTask userTask = GenerateActivityUtils.convert(taskModel, processEngine);
            process.addFlowElement(userTask);
            userTaskList.add(userTask);
        }

        // 串联新生成的任务
        for (int i = 0; i < userTaskList.size(); ++i) {
            UserTask userTask = userTaskList.get(i);
            SequenceFlow sequenceFlow = null;
            if (i == userTaskList.size() - 1) {
                // 如果是最后一个节点
                sequenceFlow = GenerateActivityUtils.generateSequenceFlow(userTask.getId() + "-->" + lastNodeId,
                        userTask.getId() + "-->" + lastNodeId, userTask.getId(), lastNodeId);
                sequenceFlow.setTargetFlowElement(process.getFlowElement(lastNodeId));
                userTask.setOutgoingFlows(Arrays.asList(sequenceFlow));
            } else {
                // 不是最后一个节点
                UserTask nextUserTask = userTaskList.get(i + 1);
                sequenceFlow = GenerateActivityUtils.generateSequenceFlow(userTask.getId() + "-->" + nextUserTask.getId(),
                        userTask.getId() + "-->" + nextUserTask.getId(), userTask.getId(), nextUserTask.getId());
                sequenceFlow.setTargetFlowElement(nextUserTask);
                userTask.setOutgoingFlows(Arrays.asList(sequenceFlow));
            }
            process.addFlowElement(sequenceFlow);
        }
        // 更新缓存
        log.info("process: {}", ToStringBuilder.reflectionToString(process, ToStringStyle.JSON_STYLE));

        processDefinitionCacheEntry.setProcess(process);

        if (fire) {
            managementService.executeCommand(new JumpCmd(taskId, targetNodeId));
        }

        if (persistenceToDB) {
            persistenceToDB(processDefinitionId, processInstanceId, processEngine, taskModelList, firstNodeId, lastNodeId);
        }
    }

    /**
     * 持久化到数据库
     * @param processDefinitionId
     * @param processInstanceId
     * @param processEngine
     * @param taskModelList
     * @param firstNodeId
     * @param lastNodeId
     */
    private void persistenceToDB(String processDefinitionId, String processInstanceId, ProcessEngine processEngine,
                                 List<TaskModel> taskModelList, String firstNodeId, String lastNodeId) {
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
        SqlSession sqlSession = processEngineConfiguration.getSqlSessionFactory().openSession();
        TmpActivityModel tmpActivityModel = getTmpActivityModel(taskModelList, firstNodeId, lastNodeId);

        CreationMapper mapper = sqlSession.getMapper(CreationMapper.class);
        ActCreation actCreation = new ActCreation();
        actCreation.setProcessInstanceId(processInstanceId);
        actCreation.setProcessDefinitionId(processDefinitionId);
        actCreation.setPropertiesText(JSON.toJSONString(tmpActivityModel));
        int insert = mapper.insert(actCreation);
        sqlSession.commit();
        sqlSession.close();
        log.info("insert 影响行数: {}", insert);
    }

    /**
     * 获得 TmpActivityModel 模型
     * @param taskModelList
     * @param firstNodeId
     * @param lastNodeId
     * @return
     */
    private TmpActivityModel getTmpActivityModel(List<TaskModel> taskModelList, String firstNodeId, String lastNodeId) {
        TmpActivityModel tmpActivityModel = new TmpActivityModel();
        tmpActivityModel.setStart(firstNodeId);
        tmpActivityModel.setEnd(lastNodeId);
        tmpActivityModel.setActivity(taskModelList);

        StringBuilder stringBuilder = new StringBuilder();
        for (TaskModel taskModel : taskModelList) {
            stringBuilder.append(taskModel.getId()).append(",");
        }
        tmpActivityModel.setActivityIds(stringBuilder.toString());
        return tmpActivityModel;
    }
}
