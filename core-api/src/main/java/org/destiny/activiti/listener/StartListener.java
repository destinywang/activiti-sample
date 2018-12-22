package org.destiny.activiti.listener;

import com.alibaba.fastjson.JSON;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngineLifecycleListener;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.apache.ibatis.session.SqlSession;
import org.destiny.activiti.addsign.AddNode;
import org.destiny.activiti.util.ActCreation;
import org.destiny.activiti.util.CreationMapper;
import org.destiny.activiti.util.TmpActivityModel;

import java.util.List;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/23 00:27
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class StartListener implements ProcessEngineLifecycleListener {

    @Override
    public void onProcessEngineBuilt(ProcessEngine processEngine) {
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
        SqlSession sqlSession = processEngineConfiguration.getSqlSessionFactory().openSession();
        CreationMapper creationMapper = sqlSession.getMapper(CreationMapper.class);
        List<ActCreation> actCreationList = creationMapper.find();
        for (ActCreation actCreation : actCreationList) {
            String processDefinitionId = actCreation.getProcessDefinitionId();
            String processInstanceId = actCreation.getProcessInstanceId();
            TmpActivityModel tmpActivityModel = JSON.parseObject(actCreation.getPropertiesText(), TmpActivityModel.class);
            AddNode addNode = new AddNode();
            addNode.addUserTask(processDefinitionId, processInstanceId, processEngine, tmpActivityModel.getActivity(), tmpActivityModel.getStart(), tmpActivityModel.getEnd(), false, false, null, null);
        }

    }


    @Override
    public void onProcessEngineClosed(ProcessEngine processEngine) {

    }
}
