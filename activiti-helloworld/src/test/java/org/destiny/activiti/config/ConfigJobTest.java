package org.destiny.activiti.config;

import org.activiti.engine.runtime.Job;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/1 21:55
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class ConfigJobTest {

    public static final Logger logger = LoggerFactory.getLogger(ConfigJobTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti_job.cfg.xml");

    @Test
    @Deployment(resources = {"my-process-job.bpmn20.xml"})
    public void test() throws InterruptedException {
        logger.info("start");
        List<Job> jobList = activitiRule.getManagementService().createTimerJobQuery().listPage(0, 100);
        for (Job job : jobList) {
            logger.info("定时任务 {}, 默认重试次数: {}", job, job.getRetries());
        }
        logger.info("jobList.size: {}", jobList.size());
        Thread.sleep(100 * 1000);
        logger.info("end");
    }
}
