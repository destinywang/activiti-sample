package org.destiny.activiti.shareniu;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/22 14:48
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class GetProcessDefinitionCacheEntryCmd implements Command<ProcessDefinitionCacheEntry> {

    private String processDefinitionId;

    public GetProcessDefinitionCacheEntryCmd(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    @Override
    public ProcessDefinitionCacheEntry execute(CommandContext commandContext) {
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModelFromCache(processDefinitionId);
        Process process = bpmnModel.getMainProcess();
        ProcessDefinitionEntity processDefinitionEntity = commandContext.getProcessDefinitionEntityManager().findById(processDefinitionId);
        ProcessDefinitionCacheEntry processDefinitionCacheEntry = new ProcessDefinitionCacheEntry(processDefinitionEntity, bpmnModel, process);
        return processDefinitionCacheEntry;
    }
}
