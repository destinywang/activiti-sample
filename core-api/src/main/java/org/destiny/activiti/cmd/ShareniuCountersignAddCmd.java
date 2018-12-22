package org.destiny.activiti.cmd;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;

import java.util.Date;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2018-12-19 18:08
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2018
 */
public class ShareniuCountersignAddCmd implements Command<Void> {

    protected String executionId;
    protected String assignee;

    public ShareniuCountersignAddCmd(String executionId, String assignee) {
        this.executionId = executionId;
        this.assignee = assignee;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ProcessEngineConfigurationImpl processEngineConfiguration = commandContext.getProcessEngineConfiguration();
        RuntimeService runtimeService = processEngineConfiguration.getRuntimeService();
        TaskService taskService = processEngineConfiguration.getTaskService();
        // ID 生成器
        IdGenerator idGenerator = processEngineConfiguration.getIdGenerator();
        // 获取 Execution 实例对象
        Execution execution = runtimeService.createExecutionQuery().executionId(executionId).singleResult();
        ExecutionEntity executionEntity = (ExecutionEntity) execution;
        // 获得父级 ExecutionEntity 实例对象
        ExecutionEntity parent = executionEntity.getParent();
        // 创建 ExecutionEntity 实例对象
        ExecutionEntity newExecution = commandContext.getExecutionEntityManager().createChildExecution(parent);
        newExecution.setActive(true);
        // 该属性表示创建的 newExecution 对象为分支, 非常重要, 不可缺少
        newExecution.setConcurrent(true);
        newExecution.setScope(false);
        Task newTask = taskService.createTaskQuery().executionId(executionId).singleResult();
        TaskEntity t = (TaskEntity) newTask;
        TaskEntity taskEntity = new TaskEntityImpl();
        taskEntity.setCreateTime(new Date());
        taskEntity.setProcessDefinitionId(t.getProcessDefinitionId());
        taskEntity.setTaskDefinitionKey(t.getTaskDefinitionKey());
        taskEntity.setProcessInstanceId(t.getProcessInstanceId());
        taskEntity.setExecutionId(newExecution.getId());
        taskEntity.setName(newTask.getName());
        String taskId = idGenerator.getNextId();
        taskEntity.setId(taskId);
        taskEntity.setRevision(0);
        taskEntity.setExecution(newExecution);
        taskEntity.setAssignee(assignee);
        taskService.saveTask(taskEntity);


//        int loopCounter = ShareniuLoopVariableUtils.getLoopVariable(newExecution, "nrOfInstances");
//        int nrOfActiveInstances = ShareniuLoopVariableUtils.getLoopVariable(newExecution, "nrOfActiveInstances");
//
//        ShareniuLoopVariableUtils.setLoopVariable(newExecution, "nrOfInstances", loopCounter + 1);
//        ShareniuLoopVariableUtils.setLoopVariable(newExecution, "nrOfActiveInstances", nrOfActiveInstances + 1);


        return null;
    }
}
