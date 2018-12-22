package org.destiny.activiti.mulitInstAdd;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.history.HistoryManager;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

import java.io.Serializable;
import java.util.Map;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/23 02:16
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class AddMulitInstanceCmd implements Command<Void>, Serializable {

    private final String NUMBER_OF_INSTANCES = "nrOfInstances";
    private final String NUMBER_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";
    private final String NUMBER_OF_COMPLETED_INSTANCES = "nrOfCompletedInstances";
    private String collectionElementIndexVariable = "loopCounter";

    private String parentExecutionId;
    private String activityId;
    private Map<String, Object> variables;

    public AddMulitInstanceCmd(String parentExecutionId, String activityId, Map<String, Object> variables) {
        this.parentExecutionId = parentExecutionId;
        this.activityId = activityId;
        this.variables = variables;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        // 获取执行实例管理器
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        // 获取二级执行实例
        ExecutionEntity secondExecution = executionEntityManager.findById(parentExecutionId);
        //
        if (secondExecution == null) {
            throw new RuntimeException("找不到二级实例数据");
        }
        // 通过二级执行流程创建三级执行实例
        ExecutionEntity childExecution = executionEntityManager.createChildExecution(secondExecution);
        // 设置
        childExecution.setCurrentFlowElement(secondExecution.getCurrentFlowElement());
        // 判断当前二级树是不是多实例节点
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(secondExecution.getProcessDefinitionId());
        Activity multiActivityElement = (Activity) bpmnModel.getFlowElement(secondExecution.getCurrentFlowElement().getId());
        MultiInstanceLoopCharacteristics loopCharacteristics = multiActivityElement.getLoopCharacteristics();
        if (loopCharacteristics == null) {
            throw new RuntimeException("没有找到 MultiInstanceLoopCharacteristics");
        }
        if (!(multiActivityElement.getBehavior() instanceof MultiInstanceActivityBehavior)) {
            throw new RuntimeException("这个节点不是多实例节点");
        }
        Integer currentNumberOfInstances = (Integer) secondExecution.getVariable(NUMBER_OF_INSTANCES);
        secondExecution.setVariableLocal(NUMBER_OF_INSTANCES, currentNumberOfInstances + 1);
        if (variables != null) {
            childExecution.setVariables(variables);
        }
        // 通知三级实例开始运行
        HistoryManager historyManager = commandContext.getHistoryManager();
        historyManager.recordActivityStart(childExecution);

        ParallelMultiInstanceBehavior behavior = (ParallelMultiInstanceBehavior) multiActivityElement.getBehavior();
        behavior.getInnerActivityBehavior().execute(childExecution);

        return null;
    }
}
