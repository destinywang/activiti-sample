package org.destiny.activiti.shareniu;

import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/22 15:04
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class JumpCmd implements Command<Void> {

    private String taskId;
    private String targetNodeId;

    public JumpCmd(String taskId, String targetNodeId) {
        this.taskId = taskId;
        this.targetNodeId = targetNodeId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        TaskEntity taskEntity = taskEntityManager.findById(taskId);
        // 执行实例 id
        String executionId = taskEntity.getExecutionId();
        String processDefinitionId = taskEntity.getProcessDefinitionId();
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        return null;
    }
}
