package org.destiny.activiti.addsign1;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.ibatis.session.SqlSession;
import org.destiny.activiti.addsign1.model.AddSign;
import org.destiny.activiti.addsign1.model.TmpActivityModel;
import org.destiny.activiti.cmd.GetProcessDefinitionCacheEntryCmd;

import java.util.List;
import java.util.Map;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2019-03-11 15:28
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2019
 */
@Deprecated
@AllArgsConstructor
public class AddSignCompleteCmd implements Command<Void> {

    private String taskId;
    private Map<String, Object> map;
    private ProcessEngine processEngine;

    @Override
    public Void execute(CommandContext commandContext) {
        TaskEntity taskEntity = commandContext.getTaskEntityManager().findById(taskId);
        String processDefinitionId = taskEntity.getProcessDefinitionId();
        ProcessEngineConfigurationImpl processEngineConfiguration = commandContext.getProcessEngineConfiguration();
        ProcessDefinitionCacheEntry processDefinitionCacheEntry = processEngineConfiguration.getManagementService().executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId, processEngine));
        if (processDefinitionCacheEntry != null) {
            BpmnModel bpmnModel = commandContext.getProcessEngineConfiguration().getRepositoryService().getBpmnModel(processDefinitionId);
            Process process = bpmnModel.getMainProcess();
            processDefinitionCacheEntry.setBpmnModel(bpmnModel);
            processDefinitionCacheEntry.setProcess(process);
        }
        SqlSession sqlSession = processEngineConfiguration.getSqlSessionFactory().openSession();
        AddSignMapper addSignMapper = sqlSession.getMapper(AddSignMapper.class);
        List<AddSign> addSignList = addSignMapper.findByProcDefId(processDefinitionId);
        for (AddSign addSign : addSignList) {
            String processInstanceId = addSign.getProcessInstanceId();
            String propertiesText = addSign.getPropertiesText();
            TmpActivityModel tmpActivityModel = JSON.parseObject(propertiesText, TmpActivityModel.class);
            AddSignService addSignService = new AddSignService();
            addSignService.addUserTask(processDefinitionId, processInstanceId, processEngine, tmpActivityModel.getActivityList(), tmpActivityModel.getFirstId(), tmpActivityModel.getLastId(), false, false, null, null);
        }
        commandContext.getProcessEngineConfiguration().getTaskService().complete(taskId, map);
        return null;
    }
}
