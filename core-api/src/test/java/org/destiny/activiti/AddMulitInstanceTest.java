package org.destiny.activiti;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.destiny.activiti.mulitInstAdd.AddMultiInstanceCmd;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/23 02:36
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
public class AddMulitInstanceTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void test() {
        BpmnModel bpmnModel = new BpmnModel();
        Process process = new Process();
        process.setId("parallel");
        bpmnModel.addProcess(process);
        StartEvent startEvent = createStartEvent();

        ValuedDataObject dataObject = new StringDataObject();
        dataObject.setName("FORM_KEY");
        dataObject.setId("FORM_KEY_ID");
        dataObject.setValue("form_key");
        process.addFlowElement(dataObject);


        // userTask
        String id = "parallelOrSign";
        String name = "parallelOrSignName";
        List<String> candidates = Arrays.asList("destiny", "freedom", "justice");
        UserTask userTask = createUserTask(id, name, candidates);
        MultiInstanceLoopCharacteristics loopCharacteristics = new MultiInstanceLoopCharacteristics();
        loopCharacteristics.setInputDataItem("users");
        loopCharacteristics.setElementVariable("user");
        loopCharacteristics.setSequential(false);
        loopCharacteristics.setCompletionCondition("${nrOfCompletedInstances==nrOfInstances}");
        userTask.setLoopCharacteristics(loopCharacteristics);
        userTask.setAssignee("${user}");

        EndEvent endEvent = createEndEvent();


        process.addFlowElement(startEvent);
        process.addFlowElement(userTask);
        process.addFlowElement(endEvent);
        process.addFlowElement(createSequenceFlow("startEvent", id, "flow1", null));
        process.addFlowElement(createSequenceFlow(id, "endEvent", "flow2", null));


//        byte[] bytes = new BpmnXMLConverter().convertToXML(bpmnModel);
//        FileUtils.copyInputStreamToFile(new ByteArrayInputStream(bytes), new File("target/parallel.xml"));

        org.activiti.engine.repository.Deployment deployment = activitiRule.getRepositoryService()
                .createDeployment()
                .addBpmnModel(process.getId() + ".bpmn", bpmnModel)
                .name(process.getName() + "_deployment")
                .deploy();

        log.info("deployment: {}", ToStringBuilder.reflectionToString(deployment, ToStringStyle.JSON_STYLE));

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("users", Arrays.asList("wk1", "wk2", "wk3"));

        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("parallel", variables);
        log.info("processInstance: {}", ToStringBuilder.reflectionToString(processInstance, ToStringStyle.JSON_STYLE));

        List<Task> taskList = activitiRule.getTaskService().createTaskQuery().list();
        log.info("当前可操作的 task 数量: {}", taskList.size());
        for (Task task : taskList) {
            log.info("task: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        }

    }

    @Test
    public void testTaskList() {
        List<Task> taskList = activitiRule.getTaskService().createTaskQuery().list();
        log.info("当前可操作的 task 数量: {}", taskList.size());
        for (Task task : taskList) {
            log.info("task: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
            log.info("ProcessVariables: {}", task.getProcessVariables());
            log.info("TaskLocalVariables: {}", task.getTaskLocalVariables());
//            log.info();
        }
    }


    @Test
    public void testAddMultiInst() {
        String taskId = "23";
        Task task = activitiRule.getTaskService().createTaskQuery().taskId(taskId).singleResult();
        Execution execution = activitiRule.getRuntimeService().createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        Map<String, Object> executionVariables = activitiRule.getRuntimeService().getVariables(execution.getId());
        log.info("executionVariables: {}", executionVariables);
        Map<String, Object> executionVariablesLocal = activitiRule.getRuntimeService().getVariablesLocal(execution.getId());
        log.info("executionVariablesLocal: {}", executionVariablesLocal);
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("user", "destiny2");
        activitiRule.getManagementService().executeCommand(new AddMultiInstanceCmd(execution.getParentId(), execution.getActivityId(), variables));
    }

    private SequenceFlow createSequenceFlow(String from, String to, String name, String conditionExpression) {
        SequenceFlow flow = new SequenceFlow();
        flow.setSourceRef(from);
        flow.setTargetRef(to);
        flow.setName(name);
        if (StringUtils.isNotEmpty(conditionExpression)) {
            flow.setConditionExpression(conditionExpression);
        }
        return flow;
    }

    private UserTask createUserTask(String id, String name, String userPkno) {
        List<String> candidateUsers = new ArrayList<String>();
        candidateUsers.add(userPkno);
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        userTask.setCandidateUsers(candidateUsers);
        return userTask;
    }

    private EndEvent createEndEvent() {
        EndEvent endEvent = new EndEvent();
        endEvent.setId("endEvent");
        return endEvent;
    }

    private StartEvent createStartEvent() {
        StartEvent startEvent = new StartEvent();
        startEvent.setId("startEvent");
        return startEvent;
    }

    private UserTask createUserTask(String id, String name, List<String> candidates) {
        UserTask userTask = new UserTask();
        userTask.setId(id);
        userTask.setName(name);
        userTask.setCandidateUsers(candidates);
        return userTask;
    }
}
