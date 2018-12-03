package org.destiny.activiti.coreapi;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;

@Slf4j
public class RuntimeServiceTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    /**
     * 根据 key 启动流程
     */
    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process.bpmn20.xml"})
    public void testStartProcess() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");
        /*
         * 每次部署流程, 对应流程的 id 和 version 都会更新
         * 在通过 key 启动流程的时候, 默认是使用最新的版本
         */
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process", variables);

        log.info("processInstance: {}", processInstance);
    }

    /**
     * 根据流程定义 id 启动流程
     */
    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process.bpmn20.xml"})
    public void testStartProcessById() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();

        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), variables);

        log.info("processInstance: {}", processInstance);
    }

    /**
     * 可以直接通过 ProcessInstanceBuilder 完成流程的设置以及启动
     */
    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process.bpmn20.xml"})
    public void testProcessBuilder() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");
        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
                .businessKey("businessKey001")
                .processDefinitionKey("my-process")
                .variables(variables)
                .start();
        log.info("processInstance: {}", processInstance);
    }

    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process.bpmn20.xml"})
    public void testVariables() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");
        variables.put("key2", "value2");
        variables.put("key3", "value3");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process", variables);
        log.info("processInstance: {}", processInstance);
        // 覆盖原有内容
        runtimeService.setVariable(processInstance.getId(), "key3", "newValue4");
        runtimeService.setVariable(processInstance.getId(), "key4", "value4");
        // 根据流程实例 id 获取流程变量
        Map<String, Object> map = runtimeService.getVariables(processInstance.getId());
        log.info("variable map: {}", map);
    }

    /**
     * 查询流程实例
     * 当一个流程启动完成后, 只要没有结束
     */
    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process.bpmn20.xml"})
    public void testProcessInstanceQuery() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process", variables);

        log.info("processInstance: {}", processInstance);
        ProcessInstance processInstance1 = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        log.info("processInstance1: {}", processInstance1);
    }

    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process.bpmn20.xml"})
    public void testExecutionQuery() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process", variables);

        log.info("processInstance: {}", processInstance);
        List<Execution> executionList = runtimeService.createExecutionQuery()
                .listPage(0, 100);

        for (Execution execution : executionList) {
            log.info("execution: {}", execution);
        }
    }

    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process-trigger.bpmn20.xml"})
    public void testTrigger() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");
        // 开始流程后流程实例就会在 receiveTask 节点等待处理
        Execution execution = runtimeService.createExecutionQuery()
                .activityId("someTask")
                .singleResult();
        log.info("execution: {}", execution);
        runtimeService.trigger(execution.getId());
        // 再次查询
        execution = runtimeService.createExecutionQuery()
                .activityId("someTask")
                .singleResult();
        log.info("execution: {}", execution);
    }

    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process-signal.bpmn20.xml"})
    public void testSignalEventReceived() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");
        // 查询数据库是否有一个正在等待信号的节点
        Execution execution = runtimeService.createExecutionQuery()
                .signalEventSubscriptionName("my-signal")   // 查询订阅了该信号的执行流
                .singleResult();
        log.info("execution: {}", execution);
        // 触发信号
        runtimeService.signalEventReceived("my-signal");

        // 重新执行查询
        execution = runtimeService.createExecutionQuery()
                .signalEventSubscriptionName("my-signal")
                .singleResult();
        log.info("execution: {}", execution);
    }
}
