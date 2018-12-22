package org.destiny.activiti.cmd;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2018-12-19 15:59
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2018
 */
public class AddTaskCmd implements Command<Void> {

    private TaskEntity taskEntity;

    public AddTaskCmd(TaskEntity taskEntity) {
        this.taskEntity = taskEntity;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        commandContext.getTaskEntityManager().insert(taskEntity);
        return null;
    }
}
