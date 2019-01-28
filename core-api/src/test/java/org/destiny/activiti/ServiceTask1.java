package org.destiny.activiti;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2019-01-21 20:18
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2019
 */
@Slf4j
public class ServiceTask1 implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.info("ServiceTask1");
        log.info("execution: {}", ToStringBuilder.reflectionToString(execution, ToStringStyle.JSON_STYLE));
//        execution.getCurrentFlowElement()
    }
}
