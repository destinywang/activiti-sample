package org.destiny.activiti.workflow;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2019-02-28 16:49
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2019
 */
public class ProcessEnginesTest {

    @Test
    public void testGetDefaultProcessEngine() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Assert.assertNotNull(processEngine);
    }

}
