package org.destiny.activiti.addsign1.util;

import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.destiny.activiti.addsign1.model.TaskModel;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2019-03-04 18:13
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2019
 */
public class ActivityUtils {

    /**
     * 生成连线
     * @param id
     * @param name
     * @param source
     * @param target
     * @return
     */
    public static SequenceFlow buildSequenceFlow(String id, String name, String source, String target) {
        SequenceFlow sequenceFlow = new SequenceFlow(source, target);
        sequenceFlow.setId(id);
        sequenceFlow.setName(name);
        return sequenceFlow;
    }

    /**
     * 创建任务
     * @param id
     * @param name
     * @param assignee
     * @param processEngine
     * @return
     */
    public static UserTask buildUserTask(String id, String name, String assignee, ProcessEngine processEngine) {
        UserTask userTask = new UserTask();
        userTask.setId(id);
        userTask.setName(name);
        userTask.setAssignee(assignee);
        userTask.setBehavior(createUserTaskBehavior(userTask, processEngine));
        return userTask;
    }

    private static UserTaskActivityBehavior createUserTaskBehavior(UserTask userTask, ProcessEngine processEngine) {
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
        ActivityBehaviorFactory activityBehaviorFactory = processEngineConfiguration.getActivityBehaviorFactory();
        return activityBehaviorFactory.createUserTaskActivityBehavior(userTask);
    }

    /**
     * 生成自定义任务模型
     * @param id
     * @param name
     * @param assignee
     * @return
     */
    public static TaskModel buildTaskModel(String id, String name, String assignee) {
        TaskModel taskModel = new TaskModel();
        taskModel.setId(id);
        taskModel.setName(name);
        taskModel.setAssignee(assignee);
        return taskModel;
    }

    /**
     * 将自定义的 TaskModel
     * @param taskModel
     * @param processEngine
     * @return
     */
    public static UserTask convertToUserTask(TaskModel taskModel, ProcessEngine processEngine) {
        return buildUserTask(taskModel.getId(), taskModel.getName(), taskModel.getAssignee(), processEngine);
    }
}
