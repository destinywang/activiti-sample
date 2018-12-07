package org.destiny.activiti.coreapi;

import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
//                .addClasspathResource("org/destiny/activiti/my-process.bpmn20.xml")
                .addClasspathResource("org/destiny/activiti/SecondApprove.bpmn20.xml")
                .deploy();                          // 完成部署

//        DeploymentBuilder deploymentBuilder2 = repositoryService.createDeployment();
//        deploymentBuilder2   // 一个部署对象就记录了一次部署
//                .name("测试部署资源2")                 // 设置名称
//                .addClasspathResource("org/destiny/activiti/my-process.bpmn20.xml")
//                .addClasspathResource("org/destiny/activiti/SecondApprove.bpmn20.xml")
//                .deploy();                          // 完成部署

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

    /**
     * 测试流程定义的暂停/挂起
     */
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

    /**
     * 用户/用户组绑定
     * repositoryService 只提供了构建关系的方式, 具体的校验逻辑需要自己完成
     * 可以取出用户/用户组信息, 自行通过逻辑判断
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = "org/destiny/activiti/my-process.bpmn20.xml")
    public void testCandidateStarter() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();
        String processDefinitionId = processDefinition.getId();
        logger.info("processDefinitionId: {}", processDefinitionId);

        // userId/groupM 是对应的用户/用户组管理服务中创建的 id
        repositoryService.addCandidateStarterUser(processDefinitionId, "user");
        repositoryService.addCandidateStarterGroup(processDefinitionId, "groupM");

        List<IdentityLink> identityLinkList = repositoryService.getIdentityLinksForProcessDefinition(processDefinitionId);
        for (IdentityLink identityLink : identityLinkList) {
            logger.info("删除前: identityLink: [{}]", identityLink);
        }

        repositoryService.deleteCandidateStarterGroup(processDefinitionId, "groupM");
        repositoryService.deleteCandidateStarterUser(processDefinitionId, "user");

        List<IdentityLink> identityLinkList1 = repositoryService.getIdentityLinksForProcessDefinition(processDefinitionId);
        for (IdentityLink identityLink : identityLinkList1) {
            logger.info("删除后: identityLink: [{}]", identityLink);
        }
    }

    @Test
    public void testDynamicDeploy() throws IOException {
        // 1. Build up the model from scratch
        BpmnModel model = new BpmnModel();
        Process process = new Process();
        model.addProcess(process);
        process.setId("my-process1");

        process.addFlowElement(createStartEvent());
        process.addFlowElement(createUserTask("task1", "First task", "fred"));
        process.addFlowElement(createUserTask("task2", "Second task", "john"));
        process.addFlowElement(createEndEvent());

        process.addFlowElement(createSequenceFlow("start", "task1"));
        process.addFlowElement(createSequenceFlow("task1", "task2"));
        process.addFlowElement(createSequenceFlow("task2", "end"));

        // 2. Generate graphical information
//        new BpmnAutoLayout(model).execute();

        // 3. Deploy the process to the engine
        Deployment deployment = activitiRule.getRepositoryService()
                .createDeployment()
                .addBpmnModel("dynamic-model.bpmn", model)
                .name("Dynamic process deployment")
                .deploy();

        // 4. Start a process instance
        ProcessInstance processInstance = activitiRule.getRuntimeService()
                .startProcessInstanceByKey("my-process1");

        // 5. Check if task is available
        List<Task> tasks = activitiRule.getTaskService()
                .createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list();

//        Assert.assertEquals(1, tasks.size());
//        Assert.assertEquals("First task", tasks.get(0).getName());
//        Assert.assertEquals("fred", tasks.get(0).getAssignee());

        // 6. Save process diagram to a file
//        InputStream processDiagram = activitiRule.getRepositoryService().getProcessDiagram(processInstance.getProcessDefinitionId());

//        FileUtils.copyInputStreamToFile(processDiagram, new File("target/diagram.png"));

        // 7. Save resulting BPMN xml to a file
        InputStream processBpmn = activitiRule.getRepositoryService().getResourceAsStream(deployment.getId(), "dynamic-model.bpmn");
        FileUtils.copyInputStreamToFile(processBpmn, new File("target/process.bpmn20.xml"));
    }

    private StartEvent createStartEvent() {
        StartEvent startEvent = new StartEvent();
        startEvent.setId("start");
        return startEvent;
    }

    private UserTask createUserTask(String id, String name, String assignee) {
        UserTask userTask = new UserTask();
        userTask.setId(id);
        userTask.setName(name);
        userTask.setAssignee(assignee);
        return userTask;
    }

    private EndEvent createEndEvent() {
        EndEvent endEvent = new EndEvent();
        endEvent.setId("end");
        return endEvent;
    }

    private SequenceFlow createSequenceFlow(String sourceRef, String targetRef) {
        SequenceFlow flow = new SequenceFlow();
        flow.setSourceRef(sourceRef);
        flow.setTargetRef(targetRef);
        return flow;
    }
}
