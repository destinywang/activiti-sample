package org.destiny.activiti.workflow.cmd;

import lombok.AllArgsConstructor;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2019/3/3 22:35
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@AllArgsConstructor
public class JumpCmd implements Command<Void> {

    private String taskId;
    private String targetNodeId;    // 目标节点

    @Override
    public Void execute(CommandContext commandContext) {
        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        TaskEntity taskEntity = taskEntityManager.findById(taskId);
        // 获取执行实例 id
        String executionId = taskEntity.getExecutionId();
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        // 获取执行实例
        ExecutionEntity executionEntity = executionEntityManager.findById(executionId);
        Process process = ProcessDefinitionUtil.getProcess(taskEntity.getProcessDefinitionId());
        FlowElement flowElement = process.getFlowElement(targetNodeId);
        if (flowElement == null) {
            throw new RuntimeException("目标节点不存在");
        }
        executionEntity.setCurrentFlowElement(flowElement);
        agenda.planContinueProcessInCompensation(executionEntity);
        return null;
    }
}
