package org.destiny.activiti;

import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2018-12-19 11:35
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2018
 */
public class SetFlowNodeAndGoCmd implements Command<Void> {

    private FlowNode flowNode;
    private String executionId;

    public SetFlowNodeAndGoCmd(FlowNode flowNode, String executionId) {
        this.flowNode = flowNode;
        this.executionId = executionId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        // 获取目标节点来源连线
        List<SequenceFlow> flows = flowNode.getIncomingFlows();
        if (CollectionUtils.isEmpty(flows)) {
            throw new ActivitiException("");
        }
        // 随便选一条连线来执行,
        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findById(executionId);
        executionEntity.setCurrentFlowElement(flows.get(0));
        commandContext.getAgenda().planTakeOutgoingSequenceFlowsOperation(executionEntity, true);
        return null;
    }
}
