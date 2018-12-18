package org.destiny.activiti;

import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p>
 *     测试在 UserTask 的定义中不关联表单时
 *     其后的 ExclusionGateway 以后依然可以正常工作
 * </p>
 * ------------------------------------------------------------------
 * design by 2018/12/15 21:14
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:activiti-context.xml"})
public class GatewayExpressSpringTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");
//    public ActivitiRule activitiRule = new ActivitiRule();

    @Autowired
    private UsersBean usersBean;

    private BpmnModel gatewayExpress() {

        BpmnModel bpmnModel = new BpmnModel();
        Process process = new Process();
        process.setId("gateway");
        bpmnModel.addProcess(process);

        StartEvent startEvent = new StartEvent();
        startEvent.setId("startEvent");
        startEvent.setName("startEvent");

        UserTask approve = new UserTask();
        approve.setAssignee("destiny");
        approve.setId("approve");
        approve.setName("approve");

        ExclusiveGateway gateway = new ExclusiveGateway();
        gateway.setId("gateway");
        gateway.setName("gateway");

        UserTask secondApprove = new UserTask();
        secondApprove.setId("secondApprove");
        secondApprove.setName("secondApprove");
        secondApprove.setAssignee("destiny");

        MultiInstanceLoopCharacteristics loopCharacteristics = new MultiInstanceLoopCharacteristics();
        loopCharacteristics.setInputDataItem("");

        EndEvent pass = new EndEvent();
        pass.setId("pass");
        pass.setName("pass");

        EndEvent reject = new EndEvent();
        reject.setId("reject");
        reject.setName("reject");

        process.addFlowElement(startEvent);
        process.addFlowElement(approve);
        process.addFlowElement(gateway);
        process.addFlowElement(secondApprove);
        process.addFlowElement(pass);
        process.addFlowElement(reject);
        process.addFlowElement(createSequence("startEvent", "approve", "flow1", "flow1", null));
        process.addFlowElement(createSequence("approve", "gateway", "flow2", "flow2", null));
        process.addFlowElement(createSequence("gateway", "secondApprove", "flow3", "flow3", "${type==\"Y\"}"));
        process.addFlowElement(createSequence("secondApprove", "pass", "flow4", "flow4", null));
        process.addFlowElement(createSequence("gateway", "reject", "flow5", "flow5", "${type==\"N\"}"));
        return bpmnModel;

    }

    private SequenceFlow createSequence(String startId, String endId, String id, String name, String conditionExpression) {
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setConditionExpression(conditionExpression);
        sequenceFlow.setSourceRef(startId);
        sequenceFlow.setTargetRef(endId);
        sequenceFlow.setId(id);
        sequenceFlow.setName(name);
        return sequenceFlow;
    }


    @Test
    @org.activiti.engine.test.Deployment(resources = {"org/destiny/activiti/my-process-spring.bpmn20.xml"})
    public void testExclusionGateway() {

        Map<String, Object> variables = new HashMap<>();
        variables.put("usersBean", usersBean);

        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", variables);
        log.info("processInstance: {}", processInstance);

        // 流程启动之后获取当前的 task
        List<Task> taskList = activitiRule.getTaskService()
                .createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list();

        log.info("数量: {}", taskList.size());
        for (Task task : taskList) {
            log.info("task: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        }

    }

    @Test
    public void testExclusionGatewayModel() {
        BpmnModel bpmnModel = new BpmnModel();
        Process process = new Process();
        process.setId("my-process");

        StartEvent startEvent = new StartEvent();
        startEvent.setId("startEvent");

        UserTask someTask = new UserTask();
        someTask.setId("someTask");
        someTask.setName("Activiti is awesome!");
        someTask.setAssignee("${user}");
        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
        multiInstanceLoopCharacteristics.setSequential(false);
        multiInstanceLoopCharacteristics.setInputDataItem("${usersBean.getUsers(name)}");
        multiInstanceLoopCharacteristics.setElementVariable("user");
        multiInstanceLoopCharacteristics.setCompletionCondition("${nrOfCompletedInstances > 0}");

        someTask.setLoopCharacteristics(multiInstanceLoopCharacteristics);

        EndEvent endEvent = new EndEvent();
        endEvent.setId("endEvent");

        SequenceFlow flow1 = createSequence("startEvent", "someTask", "flow1", "flow1", null);
        SequenceFlow flow2 = createSequence("someTask", "endEvent", "flow2", "flow2", null);

        process.addFlowElement(startEvent);
        process.addFlowElement(someTask);
        process.addFlowElement(endEvent);
        process.addFlowElement(flow1);
        process.addFlowElement(flow2);

        bpmnModel.addProcess(process);

        Deployment deployment = activitiRule.getRepositoryService().createDeployment()
                .addBpmnModel("bpmn", bpmnModel)
                .deploy();

        log.info("deployment: {}", ToStringBuilder.reflectionToString(deployment, ToStringStyle.JSON_STYLE));

        Map<String, Object> map = new HashMap<>();
        map.put("usersBean", usersBean);
        map.put("name", "wk");

        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", map);
        log.info("processInstance: {}", ToStringBuilder.reflectionToString(processInstance, ToStringStyle.JSON_STYLE));

        List<Task> taskList = activitiRule.getTaskService().createTaskQuery().list();
        log.info("当前 taskList 数量: {}", taskList.size());

        for (Task task : taskList) {
            log.info("task: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        }

        activitiRule.getTaskService().complete(taskList.get(0).getId());
        log.info("其中一个节点完成审批");

        taskList = activitiRule.getTaskService().createTaskQuery().list();
        log.info("第一个节点审批完成后 taskList 数量: {}", taskList.size());

        for (Task task : taskList) {
            log.info("第一个节点审批完成后 task: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        }
    }
}
