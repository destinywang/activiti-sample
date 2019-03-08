package org.destiny.activiti.addsign1;

import com.alibaba.fastjson.JSON;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngineLifecycleListener;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.destiny.activiti.addsign1.model.AddSign;
import org.destiny.activiti.addsign1.model.TmpActivityModel;

import java.util.List;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2019-03-06 15:06
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2019
 */
public class AddSignProcessEngineLifecycleListener implements ProcessEngineLifecycleListener {
    @Override
    public void onProcessEngineBuilt(ProcessEngine processEngine) {
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
        SqlSessionFactory sqlSessionFactory = processEngineConfiguration.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        AddSignMapper addSignMapper = sqlSession.getMapper(AddSignMapper.class);
        List<AddSign> addSignList = addSignMapper.find();
        for (AddSign addSign : addSignList) {
            String processDefinitionId = addSign.getProcessDefinitionId();
            String processInstanceId = addSign.getProcessInstanceId();
            String propertiesText = addSign.getPropertiesText();
            TmpActivityModel tmpActivityModel = JSON.parseObject(propertiesText, TmpActivityModel.class);
            AddSignService addSignService = new AddSignService();
            addSignService.addUserTask(processDefinitionId, processInstanceId, processEngine, tmpActivityModel.getActivityList(), tmpActivityModel.getFirstId(), tmpActivityModel.getLastId(), false, false, null, null);
        }
    }

    @Override
    public void onProcessEngineClosed(ProcessEngine processEngine) {

    }
}
