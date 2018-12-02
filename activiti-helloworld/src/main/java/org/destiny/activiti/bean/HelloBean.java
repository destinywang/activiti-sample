package org.destiny.activiti.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/2 12:57
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class HelloBean {

    private static final Logger logger = LoggerFactory.getLogger(HelloBean.class);

    public void sayHello() {
        logger.info("sayHello");
    }
}
