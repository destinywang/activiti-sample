package org.destiny.activiti.helloworld;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

    public static void main(String[] args) throws ParseException {
        logger.info("----- 启动我们的程序 -----");
        // 1. 创建流程引擎
        ProcessEngine processEngine = getProcessEngine();

        // 2. 部署流程定义文件
        ProcessDefinition processDefinition = getProcessDefinition(processEngine);

        // 3. 启动运行流程
        ProcessInstance processInstance = getProcessInstance(processEngine, processDefinition);

        // 4. 处理流程任务
        processTask(processEngine, processInstance);
        logger.info("----- 结束我们的程序 -----");
    }

    /**
     * 处理流程任务
     * @param processEngine
     * @param processInstance
     * @throws ParseException
     */
    private static void processTask(ProcessEngine processEngine, ProcessInstance processInstance) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        while (processInstance != null && !processInstance.isEnded()) {
            logger.info("processInstanceId: [{}]", processInstance.getId());
            logger.info("processInstance.processInstanceId: [{}]", processInstance.getProcessInstanceId());
            TaskService taskService = processEngine.getTaskService();
            List<Task> list = taskService.createTaskQuery().list();
            logger.info("待处理任务数量: [{}]", list.size());
            for (Task task : list) {
                logger.info("待处理任务: [{}]", task.getName());
                Map<String, Object> variables = getVariables(processEngine, scanner, task);
                taskService.complete(task.getId(), variables);
                processInstance = processEngine.getRuntimeService()
                        .createProcessInstanceQuery()
                        .processInstanceId(processInstance.getId())
                        .singleResult();
//                logger.info("当前 ProcessInstance :{}", JSON.toJSONString(processInstance));
            }
        }
        scanner.close();
    }

    /**
     * 获取变量
     * @param processEngine
     * @param scanner
     * @param task
     * @return
     * @throws ParseException
     */
    private static Map<String, Object> getVariables(ProcessEngine processEngine, Scanner scanner, Task task) throws ParseException {
        FormService formService = processEngine.getFormService();
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        List<FormProperty> formProperties = taskFormData.getFormProperties();
        Map<String, Object> variables = Maps.newHashMap();
        for (FormProperty property : formProperties) {
            String line = null;
            if (StringFormType.class.isInstance(property.getType())) {
                // 如果是 String 类型, 不需要做任何格式化
                logger.info("请输入 [{}] ?", property.getName());
                line = scanner.nextLine();
                variables.put(property.getId(), line);
            } else if (DateFormType.class.isInstance(property.getType())) {
                // 如果是日期类型
                logger.info("请输入 [{}] ?, 格式 (yyyy-MM-dd)", property.getName());
                line = scanner.nextLine();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = simpleDateFormat.parse(line);
                variables.put(property.getId(), date);
            } else {
                logger.info("类型不支持: {}", property.getType());
            }
            logger.info("您输入的内容是 [{}]", line);
        }
        return variables;
    }

    private static ProcessInstance getProcessInstance(ProcessEngine processEngine, ProcessDefinition processDefinition) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        logger.info("启动流程: [{}]", processInstance.getProcessDefinitionKey());
        return processInstance;
    }

    private static ProcessDefinition getProcessDefinition(ProcessEngine processEngine) {
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
        return processDefinition;
    }

    private static ProcessEngine getProcessEngine() {
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
        ProcessEngine processEngine = configuration.buildProcessEngine();
        String name = processEngine.getName();
        String version = ProcessEngine.VERSION;

        logger.info("流程引擎名称: [{}], 版本: [{}]", name, version);
        return processEngine;
    }

}
