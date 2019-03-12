package org.destiny.activiti.cmd;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.deploy.DeploymentCache;
import org.activiti.engine.impl.persistence.deploy.DeploymentManager;
import org.activiti.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.destiny.activiti.addsign1.AddSignMapper;
import org.destiny.activiti.addsign1.AddSignService;
import org.destiny.activiti.addsign1.model.AddSign;
import org.destiny.activiti.addsign1.model.TmpActivityModel;

import java.util.List;

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
@AllArgsConstructor
public class GetProcessDefinitionCacheEntryCmd implements Command<ProcessDefinitionCacheEntry> {

    private String processDefinitionId;
    private ProcessEngine processEngine;

    @Override
    public ProcessDefinitionCacheEntry execute(CommandContext commandContext) {
//        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModelFromCache(processDefinitionId);
//        Process process = bpmnModel.getMainProcess();
//        ProcessDefinitionEntity processDefinitionEntity = commandContext.getProcessDefinitionEntityManager().findById(processDefinitionId);
//        return new ProcessDefinitionCacheEntry(processDefinitionEntity, bpmnModel, process);
        ProcessEngineConfigurationImpl processEngineConfiguration = commandContext.getProcessEngineConfiguration();
        DeploymentManager deploymentManager = processEngineConfiguration.getDeploymentManager();
        DeploymentCache<ProcessDefinitionCacheEntry> processDefinitionCache = deploymentManager.getProcessDefinitionCache();
        return processDefinitionCache.get(processDefinitionId);
    }
}
