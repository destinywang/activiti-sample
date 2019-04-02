package org.destiny.activiti.jump;

import lombok.AllArgsConstructor;
import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.history.HistoryManager;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2019-04-01 15:54
 * --------------------------------------------------------------
 * 多实例跳出
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2019
 */
@AllArgsConstructor
public class MultiInstJumpCmd implements Command<Void> {

    private String taskId;
    private String targetNodeId;

    @Override
    public Void execute(CommandContext commandContext) {
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        TaskEntity taskEntity = taskEntityManager.findById(taskId);

        // 三级的执行实例对象
        String executionId = taskEntity.getExecutionId();
        ExecutionEntity realsecondExecutionEntity = executionEntityManager.findById(executionId);
        ExecutionEntity threeExecutionEntity = executionEntityManager.findById(executionId);
        String processDefinitionId = threeExecutionEntity.getProcessDefinitionId();
        Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
        Activity flowElement = (Activity) process.getFlowElement(taskEntity.getTaskDefinitionKey());
        ExecutionEntity secondExecutionEntity = threeExecutionEntity.getParent();
        Object behavior = flowElement.getBehavior();
        if (behavior instanceof MultiInstanceActivityBehavior) {
            executionEntityManager.deleteChildExecutions(secondExecutionEntity, "jump", false);
            secondExecutionEntity.setActive(true);
            secondExecutionEntity.setMultiInstanceRoot(false);
            executionEntityManager.update(secondExecutionEntity);
        }else {
            taskEntityManager.delete(taskId);
            HistoryManager historyManager = commandContext.getHistoryManager();

            historyManager.recordTaskEnd(taskId, "shareniu-jump");
            historyManager.recordActivityEnd(realsecondExecutionEntity, "shareniu-jump");
        }
        Activity targetFlowElement = (Activity) process.getFlowElement(targetNodeId);
        behavior = targetFlowElement.getBehavior();
        secondExecutionEntity.setCurrentFlowElement(targetFlowElement);
        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        if (behavior instanceof MultiInstanceActivityBehavior) {
            agenda.planContinueMultiInstanceOperation(secondExecutionEntity);
        }else {
            realsecondExecutionEntity.setCurrentFlowElement(targetFlowElement);
            agenda.planContinueProcessInCompensation(realsecondExecutionEntity);
        }
        return null;
    }
}
