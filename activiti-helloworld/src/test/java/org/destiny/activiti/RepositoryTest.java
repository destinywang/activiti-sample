package org.destiny.activiti;

import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.UUID;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2019/2/22 22:37
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
public class RepositoryTest {

    volatile

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void test() {
        Deployment deployment = activitiRule.getRepositoryService()
                .createDeployment()
                .name("my-process")
                .addClasspathResource("my-process.bpmn20.xml")
                .deploy();

        log.info("deployment: {}", deployment);
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        log.info("processInstance: {}", processInstance);
    }

    @Test
    public void testComplete() {
        String taskId = "2508";
        activitiRule.getTaskService().complete(taskId);
    }


    @Test
    public void buildBpmnModel() {
        BpmnModel bpmnModel = new BpmnModel();
        Process process = new Process();
        process.setId("my-process");
        process.setName("my-process");
        process.setExecutable(true);
        bpmnModel.addProcess(process);

        StartEvent startEvent = new StartEvent();
        startEvent.setId("startEvent");
        startEvent.setName("startEvent");
        process.addFlowElement(startEvent);


        UserTask userTask1 = buildUserTask("userTask1", "提交申请");
        process.addFlowElement(userTask1);
        UserTask userTask2 = buildUserTask("userTask2", "部门经理审批");
        process.addFlowElement(userTask2);
        UserTask userTask3 = buildUserTask("userTask3", "总经理审批");
        process.addFlowElement(userTask3);


        ExclusiveGateway gateway = new ExclusiveGateway();
        gateway.setId("gateway1");
        gateway.setName("gateway1");
        process.addFlowElement(gateway);

        EndEvent endEvent1 = buildEndEvent("endEvent1", "endEvent1");
        process.addFlowElement(endEvent1);

        EndEvent endEvent2 = buildEndEvent("endEvent2", "endEvent2");
        process.addFlowElement(endEvent2);

        SequenceFlow sequenceFlow1 = buildSequenceFlow("flow1", "flow1", startEvent.getId(), userTask1.getId(), null);
        SequenceFlow sequenceFlow2 = buildSequenceFlow("flow2", "flow2", userTask1.getId(), gateway.getId(), null);
//        buildSequenceFlow("flow3", "flow3", gateway, )
    }

    private EndEvent buildEndEvent(String id, String name) {
        EndEvent endEvent = new EndEvent();
        endEvent.setId(id);
        endEvent.setName(name);
        return endEvent;
    }

    private UserTask buildUserTask(String id, String name) {
        UserTask userTask = new UserTask();
        userTask.setId(id);
        userTask.setName(name);
        return userTask;
    }

    private SequenceFlow buildSequenceFlow(String id, String name, String srcId, String targetId, String conditionExpression) {
        SequenceFlow sequenceFlow = new SequenceFlow(srcId, targetId);
        sequenceFlow.setId(id);
        sequenceFlow.setName(name);
        sequenceFlow.setConditionExpression(conditionExpression);
        return sequenceFlow;
    }

    @Test
    public void testUUID() {
        String uuid = UUID.randomUUID().toString();
        System.out.println(uuid);
    }
}
