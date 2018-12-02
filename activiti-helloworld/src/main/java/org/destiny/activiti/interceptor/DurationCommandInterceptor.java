package org.destiny.activiti.interceptor;

import org.activiti.engine.impl.interceptor.AbstractCommandInterceptor;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p>
 *     执行时间
 * </p>
 * ------------------------------------------------------------------
 * design by 2018/12/2 10:02
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class DurationCommandInterceptor extends AbstractCommandInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(DurationCommandInterceptor.class);

    @Override
    public <T> T execute(CommandConfig config, Command<T> command) {
        // 记录当前时间
        long start = System.currentTimeMillis();
        try {
            return this.getNext().execute(config, command);
        } finally {
            long duration = System.currentTimeMillis() - start;
            logger.info("{} 执行时长: {} 毫秒", command.getClass().getSimpleName(), duration);
        }
    }
}
