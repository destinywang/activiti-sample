package org.destiny.activiti.helloworld;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p>
 *     启动类
 * </p>
 * ------------------------------------------------------------------
 * design by 2018/12/1 16:48
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Data
@Slf4j
public class DemoMain {

    private static final Logger logger = LoggerFactory.getLogger(DemoMain.class);

    public static void main(String[] args) {
        logger.info("----- 启动我们的程序 -----");
        // 1. 创建流程引擎
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
        ProcessEngine processEngine = configuration.buildProcessEngine();
        String name = processEngine.getName();
        String version = ProcessEngine.VERSION;

        logger.info("流程引擎名称: [{}], 版本: [{}]", name, version);

        // 2. 部署流程定义文件
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("SecondApprove.bpmn20.xml");
        Deployment deployment = deploymentBuilder.deploy();

        String deploymentId = deployment.getId();
        // deploymentId: 1
        logger.info("deploymentId: [{}]", deploymentId);
        ProcessDefinition processDefinition = repositoryService.
                createProcessDefinitionQuery().
                deploymentId(deploymentId)
                .singleResult();

        // processDefinition.getId() 是 SecondApprove:1:4, 根据部署 id 和流程 id 组装出的数据
        logger.info("流程定义文件: [{}], 流程 id: [{}]", processDefinition.getName(), processDefinition.getId());

        // 3. 启动运行流程
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        logger.info("启动流程: [{}]", processInstance.getProcessDefinitionKey());

        // 4. 处理流程任务
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().list();
        for (Task task : list) {
            logger.info("待处理任务: [{}]", task.getName());
        }
        logger.info("待处理任务数量: [{}]", list.size());

        logger.info("----- 结束我们的程序 -----");
    }

}
