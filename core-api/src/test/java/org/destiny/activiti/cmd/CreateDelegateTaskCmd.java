package org.destiny.activiti.cmd;

import org.activiti.engine.impl.cmd.NewTaskCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2018-12-19 11:55
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2018
 */
public class CreateDelegateTaskCmd implements Command<TaskEntity> {

    private Task currTask;
    private String userId;

    public CreateDelegateTaskCmd(Task currTask, String userId) {
        this.currTask = currTask;
        this.userId = userId;
    }

    @Override
    public TaskEntity execute(CommandContext commandContext) {
        TaskEntity task = commandContext.getTaskEntityManager().create();
//        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findById(currTask.getExecutionId());
//        task.setExecution(executionEntity);
        task.setAssignee(userId);
        task.setCategory(currTask.getCategory());
        task.setClaimTime(currTask.getClaimTime());
        task.setCreateTime(currTask.getCreateTime());
        task.setDescription(currTask.getDescription());
        task.setDueDate(currTask.getDueDate());
        task.setExecutionId(currTask.getExecutionId());
        task.setFormKey(currTask.getFormKey());
        task.setName(currTask.getName());
        task.setOwner(currTask.getOwner());
//        task.setParentTaskId(currTask.getId());
        task.setPriority(currTask.getPriority());
        task.setProcessDefinitionId(currTask.getProcessDefinitionId());
        task.setProcessInstanceId(currTask.getProcessInstanceId());
        task.setTaskDefinitionKey(currTask.getTaskDefinitionKey());
        task.setVariablesLocal(currTask.getTaskLocalVariables());
        task.setTenantId(currTask.getTenantId());
        task.setDeleted(false);
        task.setRevision(0);
        return task;
    }
}
