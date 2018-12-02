package org.destiny.activiti.coreapi;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.test.ActivitiRule;
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
 * design by 2018/12/2 15:52
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class RepositoryServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void testRepository() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        DeploymentBuilder deploymentBuilder1 = repositoryService.createDeployment();
        deploymentBuilder1   // 一个部署对象就记录了一次部署
                .name("测试部署资源1")                 // 设置名称
                .addClasspathResource("org/destiny/activiti/my-process.bpmn20.xml")
                .addClasspathResource("org/destiny/activiti/SecondApprove.bpmn20.xml")
                .deploy();                          // 完成部署

        DeploymentBuilder deploymentBuilder2 = repositoryService.createDeployment();
        deploymentBuilder2   // 一个部署对象就记录了一次部署
                .name("测试部署资源2")                 // 设置名称
                .addClasspathResource("org/destiny/activiti/my-process.bpmn20.xml")
                .addClasspathResource("org/destiny/activiti/SecondApprove.bpmn20.xml")
                .deploy();                          // 完成部署

        // 查询部署对象
        List<Deployment> deploymentList = repositoryService.createDeploymentQuery()
//                .deploymentId(deployment1.getId())
                .orderByDeploymenTime().asc()
                .list();

        logger.info("size of deploymentList: {}", deploymentList.size());
        for (Deployment deployment : deploymentList) {
            logger.info("deployment: {}", deployment);
        }


        // 流程定义
        List<ProcessDefinition> processDefinitionList = repositoryService
                .createProcessDefinitionQuery()
//                .deploymentId(deployment1.getId())
                .orderByProcessDefinitionKey().asc()
                .listPage(0, 100);
        for (ProcessDefinition processDefinition : processDefinitionList) {
            logger.info("processDefinition: {}, version: {}, key: {}, name: {}",
                    processDefinition, processDefinition.getVersion(), processDefinition.getKey(), processDefinition.getName());
        }

    }

    @Test
    @org.activiti.engine.test.Deployment(resources = "org/destiny/activiti/my-process.bpmn20.xml")
    public void testSuspend() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();
        String processDefinitionId = processDefinition.getId();
        logger.info("processDefinitionId: {}", processDefinitionId);

        repositoryService.suspendProcessDefinitionById(processDefinitionId);

        try {
            logger.info("开始启动");
            activitiRule.getRuntimeService().startProcessInstanceById(processDefinitionId);
            logger.info("启动成功");
        } catch (Exception e) {
            logger.error("启动失败, 原因: {}", e.getMessage());
        }

        repositoryService.activateProcessDefinitionById(processDefinitionId);
        logger.info("激活后开始启动");
        activitiRule.getRuntimeService().startProcessInstanceById(processDefinitionId);
        logger.info("激活后启动成功");
    }
}
