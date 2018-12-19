package org.destiny.activiti;

import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManagerImpl;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2018-12-19 11:29
 * --------------------------------------------------------------
 * 删除当前运行时任务命令, 并返回当前任务的执行对象 id
 * 这里继承了 NeedsActiveTaskCmd，主要是是很多跳转业务场景下，要求不能时挂起任务。可以直接继承Command即可
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2018
 */
public class DeleteTaskCmd extends NeedsActiveTaskCmd<String> {
    public DeleteTaskCmd(String taskId) {
        super(taskId);
    }

    @Override
    protected String execute(CommandContext commandContext, TaskEntity task) {
        // 获取所需服务
        TaskEntityManagerImpl taskEntityManager = (TaskEntityManagerImpl)commandContext.getTaskEntityManager();
        // 获取当前任务的来源任务以及来源节点信息
        ExecutionEntity executionEntity = task.getExecution();
        // 删除当前任务, 来源任务
        taskEntityManager.deleteTask(task, "jump reason", false, false);
        //
        return executionEntity.getId();
    }
}
