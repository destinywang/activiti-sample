package org.destiny.activiti.shareniu;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.test.ActivitiRule;
import org.apache.ibatis.session.SqlSession;
import org.destiny.activiti.util.ActCreation;
import org.destiny.activiti.mapper.CreationMapper;
import org.destiny.activiti.util.TmpActivityModel;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Date;
import java.util.List;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/22 16:05
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
public class ActCreationTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void testList() {
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) activitiRule.getProcessEngine().getProcessEngineConfiguration();
        SqlSession sqlSession = processEngineConfiguration.getSqlSessionFactory().openSession();
        List<ActCreation> actCreationList = sqlSession.selectList("org.destiny.activiti.mapper.CreationMapper.find", "my-process:1:3");
        log.info("actCreationList: {}", JSON.toJSONString(actCreationList));
    }

    @Test
    public void testInsert() {
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) activitiRule.getProcessEngine().getProcessEngineConfiguration();
        SqlSession sqlSession = processEngineConfiguration.getSqlSessionFactory().openSession();
        CreationMapper creationMapper = sqlSession.getMapper(CreationMapper.class);

        ActCreation actCreation = new ActCreation();
        actCreation.setProcessDefinitionId("processDefinitionId");
        actCreation.setProcessInstanceId("processInstanceId");
        actCreation.setCreateTime(new Date(System.currentTimeMillis()));

        TmpActivityModel tmpActivityModel = new TmpActivityModel();
        tmpActivityModel.setStart("start");
        tmpActivityModel.setEnd("end");
        actCreation.setPropertiesText(JSON.toJSONString(tmpActivityModel));

        creationMapper.insert(actCreation);
        sqlSession.commit();
        sqlSession.close();
    }
}
