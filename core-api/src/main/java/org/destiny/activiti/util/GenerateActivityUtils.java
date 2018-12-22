package org.destiny.activiti.util;

import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/22 15:20
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class GenerateActivityUtils {

    /**
     * 生成连线
     *
     * @param id
     * @param name
     * @param source
     * @param target
     * @return
     */
    public static SequenceFlow generateSequenceFlow(String id, String name, String source, String target) {
        SequenceFlow sequenceFlow = new SequenceFlow(source, target);
        sequenceFlow.setId(id);
        sequenceFlow.setName(name);
        return sequenceFlow;
    }

    /**
     * 创建用户任务
     *
     * @param id
     * @param name
     * @param assignee
     * @param processEngine
     * @return
     */
    public static UserTask generateUserTask(String id, String name, String assignee, ProcessEngine processEngine) {
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
     * 生成自定义的任务模型
     *
     * @param id
     * @param name
     * @param assignee
     * @return
     */
    public static TaskModel generateTaskModel(String id, String name, String assignee) {
        return new TaskModel(id, name, assignee);
    }

    /**
     * 将自定义的 TaskModel 转换为 UserTask
     * @param taskModel
     * @param processEngine
     * @return
     */
    public static UserTask convert(TaskModel taskModel, ProcessEngine processEngine) {
        return generateUserTask(taskModel.getId(), taskModel.getName(), taskModel.getDoUserId(), processEngine);
    }
}
