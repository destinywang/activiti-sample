package org.destiny.activiti.config;

import lombok.extern.slf4j.Slf4j;
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
 * design by 2018/12/1 19:44
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class ConfigTest {

    private static final Logger logger = LoggerFactory.getLogger(ConfigTest.class);

    @Test
    public void testConfig1() {
        // 从 activiti.cfg.xml 获取 id 为 processEngineConfiguration 的 bean
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault();

        // org.activiti.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration@be35cd9
        logger.info("configuration: {}", configuration);
    }

    @Test
    public void testConfig2() {
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();

        // org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration@48fa0f47
        logger.info("configuration: {}", configuration);
    }
}
