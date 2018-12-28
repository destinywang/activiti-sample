package org.destiny.activiti.listener;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.apache.ibatis.session.SqlSession;
import org.destiny.activiti.mapper.CreationMapper;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/23 01:40
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
@Setter
@Getter
public class EventListener implements ActivitiEventListener {

    private ProcessEngineConfigurationImpl processEngineConfiguration;

    @Override
    public void onEvent(ActivitiEvent event) {
        switch (event.getType()) {
            case PROCESS_COMPLETED:
                log.info("PROCESS_COMPLETED");
                ActivitiEntityEventImpl activitiEntityEvent = (ActivitiEntityEventImpl) event;
                String processInstanceId = activitiEntityEvent.getProcessInstanceId();
                SqlSession sqlSession = processEngineConfiguration.getSqlSessionFactory().openSession();
                CreationMapper creationMapper = sqlSession.getMapper(CreationMapper.class);
                // TODO Mapper delete
                break;
        }
    }

    /**
     * @return whether or not the current operation should fail when this listeners execution throws an exception.
     */
    @Override
    public boolean isFailOnException() {
        return false;
    }
}
