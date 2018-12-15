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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/15 21:14
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
public class GatewayExpressTest {

    @Rule
//    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");
    public ActivitiRule activitiRule = new ActivitiRule();

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
    public void testExclusionGateway() {
        BpmnModel bpmnModel = new BpmnModel();
        Process process = new Process();
        process.setId("ExclusionGateway");
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

        Deployment deploy = activitiRule.getRepositoryService()
                .createDeployment()
                .addBpmnModel(process.getId() + ".bpmn", bpmnModel)
                .name(process.getName())
                .deploy();

        log.info("deploy: {}", ToStringBuilder.reflectionToString(deploy, ToStringStyle.JSON_STYLE));

        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("ExclusionGateway");
        log.info("processInstance: {}", processInstance);

        // 流程启动之后获取当前的 task
        Task task = activitiRule.getTaskService()
                .createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        Assert.assertEquals("approve", task.getName());

        log.info("approve task: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));

        // approve task 提交一个表单
        Map<String, Object> variables = new HashMap<>();
        variables.put("type", "Y");
        activitiRule.getTaskService().complete(task.getId(), variables);

        processInstance = activitiRule.getRuntimeService().createProcessInstanceQuery().singleResult();
        List<Task> taskList = activitiRule.getTaskService()
                .createTaskQuery()
                .list();
        log.info("approve 节点提交后的 task 总数: [{}]", taskList.size());
        for (Task task1 : taskList) {
            log.info("task: {}", ToStringBuilder.reflectionToString(task1, ToStringStyle.JSON_STYLE));
        }

//        Assert.assertTrue(processInstance.isEnded());
    }

}
