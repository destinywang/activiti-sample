package org.destiny.activiti.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/2 00:29
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class MDCErrorDelegate implements JavaDelegate {

    public static final Logger logger = LoggerFactory.getLogger(MDCErrorDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        logger.info("MDCErrorDelegate.execute");
        throw new RuntimeException("test only");
    }
}
