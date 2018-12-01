package org.destiny.activiti.config;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/1 20:37
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
public class ConfigDBTest {

    public static final Logger logger = LoggerFactory.getLogger(ConfigDBTest.class);

    @Test
    public void testConfig1() {
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault();
        logger.info("configuration = {}", configuration);

        ProcessEngine processEngine = configuration.buildProcessEngine();
        logger.info("获取流程引擎: {}", processEngine.getName());
        processEngine.close();
    }


    @Test
    public void testConfig2() {
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti_druid.cfg.xml");
        logger.info("configuration = {}", configuration);

        ProcessEngine processEngine = configuration.buildProcessEngine();
        logger.info("获取流程引擎: {}", processEngine.getName());
        processEngine.close();
    }
}
