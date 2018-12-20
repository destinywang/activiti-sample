package org.destiny.activiti;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DiagramEdge;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.destiny.model.SysWorkflow;
import org.destiny.model.SysWorkflowStep;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class WorkflowTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void dynamicDeploy() throws IOException {
        SysWorkflow workflow = new SysWorkflow();
        workflow.setName("动态生成流程");
        workflow.setContent("动态生成流程 content");
        workflow.setId(1L);

        List<SysWorkflowStep> stepList = new ArrayList<>();
        SysWorkflowStep step1 = new SysWorkflowStep();
        step1.setCreateTime(new Date());
        step1.setWorkflowId(workflow.getId());
        step1.setId(2L);
        step1.setType(2);
        step1.setRolePkno("destiny");

        SysWorkflowStep step2 = new SysWorkflowStep();
        step2.setCreateTime(new Date());
        step2.setWorkflowId(workflow.getId());
        step2.setId(3L);
        step2.setType(1);
        step2.setRolePkno("destiny");

        stepList.add(step1);
        stepList.add(step2);


        // 1. 建立模型
        BpmnModel model = new BpmnModel();
        Process process = new Process();
        model.addProcess(process);
        process.setId("news");
        process.setName(workflow.getName());
        process.setDocumentation(workflow.getContent());

        // 添加流程
        // 开始节点
        process.addFlowElement(createStartEvent());
        for (int i = 0; i < stepList.size(); ++i) {
            SysWorkflowStep step = stepList.get(i);
            // 判断是否会签
            if (step.getType() == 1) {
                // 如果是会签, 加入并行网关
                process.addFlowElement(createParallelGateway("parallelGateway-fork" + i, "并行网关-分支" + i));
                // 获取角色下所有用户
                List<String> userList = getUserRole(step.getRolePkno());
                for (int u = 0; u < userList.size(); ++u) {
                    // 并行网关分支的审核节点
                    process.addFlowElement(createUserTask("userTask" + i + u, "并行网关分支用户审核节点" + i + u, userList.get(u)));
                }
                // 并行网关-汇聚
                process.addFlowElement(createParallelGateway("parallelGateway-join" + i, "并行网关-汇聚" + i));
            } else {
                // 普通流转
                // 审核节点
                process.addFlowElement(createGroupTask("task" + i, "组审核节点" + i, step.getRolePkno()));
                // 回退节点
                process.addFlowElement(createUserTask("repulse" + i, "回退节点" + i, "${startUserId}"));
            }
        }
        // 结束节点
        process.addFlowElement(createEndEvent());

        //连线
        for (int y = 0; y < stepList.size(); y++) {
            SysWorkflowStep step = stepList.get(y);
            //是否会签
            if (step.getType() == 1) {
                //会签
                //判断是否第一个节点
                if (y == 0) {
                    //开始节点和并行网关-分支连线
                    process.addFlowElement(createSequenceFlow("startEvent", "parallelGateway-fork" + y, "开始节点到并行网关-分支" + y, ""));
                } else {
                    //审核节点或者并行网关-汇聚到并行网关-分支
                    //判断上一个节点是否是会签
                    if (stepList.get(y - 1).getType() == 1) {
                        process.addFlowElement(createSequenceFlow("parallelGateway-join" + (y - 1), "parallelGateway-fork" + y, "并行网关-汇聚到并行网关-分支" + y, ""));
                    } else {
                        process.addFlowElement(createSequenceFlow("task" + (y - 1), "parallelGateway-fork" + y, "上一个审核节点到并行网关-分支" + y, ""));
                    }
                }
                //并行网关-分支和会签用户连线，会签用户和并行网关-汇聚连线
                List<String> userList = getUserRole(step.getRolePkno());
                for (int u = 0; u < userList.size(); u++) {
                    process.addFlowElement(createSequenceFlow("parallelGateway-fork" + y, "userTask" + y + u, "并行网关-分支到会签用户" + y + u, ""));
                    process.addFlowElement(createSequenceFlow("userTask" + y + u, "parallelGateway-join" + y, "会签用户到并行网关-汇聚", ""));
                }
                //最后一个节点  并行网关-汇聚到结束节点
                if (y == (stepList.size() - 1)) {
                    process.addFlowElement(createSequenceFlow("parallelGateway-join" + y, "endEvent", "并行网关-汇聚到结束节点", ""));
                }
            } else {
                //普通流转
                //第一个节点
                if (y == 0) {
                    //开始节点和审核节点1
                    process.addFlowElement(createSequenceFlow("startEvent", "task" + y, "开始节点到审核节点" + y, ""));
                } else {
                    //判断上一个节点是否会签
                    if (stepList.get(y - 1).getType() == 1) {
                        //会签
                        //并行网关-汇聚到审核节点
                        process.addFlowElement(createSequenceFlow("parallelGateway-join" + (y - 1), "task" + y, "并行网关-汇聚到审核节点" + y, ""));
                    } else {
                        //普通
                        process.addFlowElement(createSequenceFlow("task" + (y - 1), "task" + y, "审核节点" + (y - 1) + "到审核节点" + y, "${flag=='true'}"));
                    }
                }
                //是否最后一个节点
                if (y == (stepList.size() - 1)) {
                    //审核节点到结束节点
                    process.addFlowElement(createSequenceFlow("task" + y, "endEvent", "审核节点" + y + "到结束节点", "${flag=='true'}"));
                }
                //审核节点到回退节点
                process.addFlowElement(createSequenceFlow("task" + y, "repulse" + y, "审核不通过-打回" + y, "${flag=='false'}"));
                process.addFlowElement(createSequenceFlow("repulse" + y, "task" + y, "回退节点到审核节点" + y, ""));
            }
        }

        // 流程部署
        Deployment deployment = activitiRule.getRepositoryService()
                .createDeployment()
                .addBpmnModel(process.getId() + ".bpmn", model)
                .name(process.getName() + "_deployment")
                .deploy();

        // 启动一个实例
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey(process.getId());

        // 获取流程任务
        List<Task> taskList = activitiRule.getTaskService()
                .createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list();


//        ProcessEngineConfiguration processEngineConfiguration = activitiRule.getProcessEngine().getProcessEngineConfiguration();
//        ProcessDiagramGenerator processDiagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
//        InputStream inputStream = processDiagramGenerator.generateJpgDiagram(model);
//
//        FileUtils.copyInputStreamToFile(inputStream, new File("target/deployments/" + process.getId() + ".jpg"));
        // 将流程图保存到本地
//        InputStream processDiagram = activitiRule.getRepositoryService().getProcessDiagram(processInstance.getProcessDefinitionId());
//        FileUtils.copyInputStreamToFile(processDiagram, new File("target/deployments/" + process.getId() + ".png"));

        // 将 XML 保存到本地
        InputStream processBpmn = activitiRule.getRepositoryService().getResourceAsStream(deployment.getId(), process.getId() + ".bpmn");
        FileUtils.copyInputStreamToFile(processBpmn, new File("target/deployments/" + process.getId() + ".bpmn"));
    }

    private List<String> getUserRole(String rolePkno) {
        List<String> userList = new ArrayList<>();
        userList.add("destiny");
        userList.add("camery");
        return userList;
    }

    /**
     * 开始节点
     *
     * @return
     */
    private StartEvent createStartEvent() {
        StartEvent startEvent = new StartEvent();
        startEvent.setId("startEvent");
        return startEvent;
    }

    /**
     * 结束节点
     *
     * @return
     */
    private EndEvent createEndEvent() {
        EndEvent endEvent = new EndEvent();
        endEvent.setId("endEvent");
        return endEvent;
    }

    /**
     * 并行网关
     *
     * @param id
     * @param name
     * @return
     */
    private ParallelGateway createParallelGateway(String id, String name) {
        ParallelGateway gateway = new ParallelGateway();
        gateway.setId(id);
        gateway.setName(name);
        return gateway;
    }

    /**
     * 排他网关
     *
     * @param id
     * @param name
     * @return
     */
    private ExclusiveGateway createExclusiveGateway(String id, String name) {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId(id);
        exclusiveGateway.setName(name);
        return exclusiveGateway;
    }

    /**
     * 连线
     *
     * @param from
     * @param to
     * @param name
     * @param conditionExpression
     * @return
     */
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

    /**
     * 任务节点 - 锁定者
     *
     * @param id
     * @param name
     * @param assignee
     * @return
     */
    private UserTask createAssigneeTask(String id, String name, String assignee) {
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        userTask.setAssignee(assignee);
        return userTask;
    }

    /**
     * 任务节点 - 用户
     *
     * @param id
     * @param name
     * @param userPkno
     * @return
     */
    private UserTask createUserTask(String id, String name, String userPkno) {
        List<String> candidateUsers = new ArrayList<String>();
        candidateUsers.add(userPkno);
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        userTask.setCandidateUsers(candidateUsers);
        return userTask;
    }

    private UserTask createGroupTask(String id, String name, String candidateGroup) {
        List<String> candidateGroups = new ArrayList<String>();
        candidateGroups.add(candidateGroup);
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        userTask.setCandidateGroups(candidateGroups);
        return userTask;
    }

    @Test
    public void createMultiInstance() throws IOException {
        BpmnModel bpmnModel = new BpmnModel();
        Process process = new Process();
        process.setId("parallel");
        bpmnModel.addProcess(process);
        StartEvent startEvent = createStartEvent();

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

        Deployment deployment = activitiRule.getRepositoryService()
                .createDeployment()
                .addBpmnModel(process.getId() + ".bpmn", bpmnModel)
                .name(process.getName() + "_deployment")
                .deploy();

        log.info("deployment: {}", ToStringBuilder.reflectionToString(deployment, ToStringStyle.JSON_STYLE));

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("users", Arrays.asList("wk1", "wk2"));

        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("parallel", variables);
        log.info("processInstance: {}", ToStringBuilder.reflectionToString(processInstance, ToStringStyle.JSON_STYLE));

        List<Task> taskList = activitiRule.getTaskService().createTaskQuery().list();
        log.info("当前可操作的 task 数量: {}", taskList.size());
        for (Task task : taskList) {
            log.info("task: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        }


        List<Task> listDestiny = activitiRule.getTaskService().createTaskQuery().taskCandidateUser("destiny").list();
        for (Task task : listDestiny) {
            log.info("taskDestiny: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        }
        List<Task> listFreedom = activitiRule.getTaskService().createTaskQuery().taskCandidateUser("freedom").list();
        for (Task task : listFreedom) {
            log.info("taskFreedom: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        }
        List<Task> listJustice = activitiRule.getTaskService().createTaskQuery().taskCandidateUser("justice").list();
        for (Task task : listJustice) {
            log.info("taskJustice: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        }
        List<Task> listWk1 = activitiRule.getTaskService().createTaskQuery().taskAssignee("wk1").list();
        for (Task task : listWk1) {
            log.info("taskWk1: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        }
        List<Task> listWk2 = activitiRule.getTaskService().createTaskQuery().taskAssignee("wk2").list();
        for (Task task : listWk2) {
            log.info("taskWk2: {}", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        }

        byte[] bytes = new BpmnXMLConverter().convertToXML(bpmnModel, "utf-8");
        log.info("byte length: {}", bytes.length);

        FileUtils.copyInputStreamToFile(new ByteArrayInputStream(bytes), new File("target/parallel.xml"));

        ProcessDiagramGenerator processDiagramGenerator = activitiRule.getProcessEngine().getProcessEngineConfiguration().getProcessDiagramGenerator();
        log.info("processDiagramGenerator: {}", processDiagramGenerator);
//        InputStream inputStream = processDiagramGenerator.generatePngDiagram(bpmnModel);
//        FileUtils.copyInputStreamToFile(inputStream, new File("target/parallel.png"));

//        log.info("由 freedom 成为办理人");
//        activitiRule.getTaskService().claim();
    }

    private UserTask createUserTask(String id, String name, List<String> candidates) {
        UserTask userTask = new UserTask();
        userTask.setId(id);
        userTask.setName(name);
        userTask.setCandidateUsers(candidates);
        return userTask;
    }

    /**
     * 撤回
     */
    @Test
    public void testRecall() {
        String taskId = "1";
        // 获得当前任务
        HistoricTaskInstance taskInstance = activitiRule.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .singleResult();

        // 根据流程 id 查询代办任务中流程信息
        Task task = activitiRule.getTaskService()
                .createTaskQuery()
                .processInstanceId(taskInstance.getProcessInstanceId())
                .singleResult();

        // 取回流程节点, 当前任务 id, 取回任务 id
        recall(task.getId(), taskInstance.getId());
    }

    /**
     * 撤回流程
     *
     * @param taskId
     * @param currTaskId
     */
    private void recall(String taskId, String currTaskId) {
//        if (StringUtils.isEmpty(currTaskId)) {
//            throw
//        }
        List<Task> taskList = findTaskListByKey(findProcessInstanceByTaskId(taskId).getId(), findTaskById(taskId).getTaskDefinitionKey());
        for (Task task : taskList) {

        }

    }

    private List<Task> findTaskListByKey(String processInstanceId, String taskDefinitionKey) {
        return activitiRule.getTaskService().createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(taskDefinitionKey)
                .list();
    }

    private ProcessInstance findProcessInstanceByTaskId(String taskId) {
        ProcessInstance processInstance = activitiRule.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(findTaskById(taskId).getProcessInstanceId()).singleResult();
        return processInstance;
    }

    private Task findTaskById(String taskId) {
        Task task = activitiRule.getTaskService()
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
        return task;
    }

    private void commitProcess(String taskId, Map<String, Object> variables,
                               String activityId) throws Exception {
        if (variables == null) {
            variables = new HashMap<String, Object>();
        }
        // 跳转节点为空，默认提交操作
        if (StringUtils.isEmpty(activityId)) {
            activitiRule.getTaskService().complete(taskId, variables);
        } else {
            // 流程转向操作
//            turnTransition(taskId, activityId, variables);
        }
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = {"org/destiny/activiti/my-process-second.bpmn20.xml"})
    public void testSecond() {
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        Map<String, Object> map = Maps.newHashMap();
        map.put("_ACTION", "YES");
        activitiRule.getTaskService().complete(task.getId(), map);

        List<Task> list = activitiRule.getTaskService().createTaskQuery().list();
        for (Task task1 : list) {
            log.info("task: {}", ToStringBuilder.reflectionToString(task1, ToStringStyle.JSON_STYLE));
        }

        task = activitiRule.getTaskService().createTaskQuery().singleResult();
//        Map<String, Object> taskLocalVariables = task.getTaskLocalVariables();
//        log.info("taskLocalVariables: {}", taskLocalVariables);
        map = Maps.newHashMap();
        map.put("_ACTION", "NO");
        activitiRule.getTaskService().complete(task.getId(), map);

        HistoricProcessInstance historicProcessInstance = activitiRule.getHistoryService().createHistoricProcessInstanceQuery().singleResult();
        log.info("historicProcessInstance: {}", ToStringBuilder.reflectionToString(historicProcessInstance, ToStringStyle.JSON_STYLE));
    }
}
