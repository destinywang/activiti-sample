package org.destiny.activiti.coreapi;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.FormService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FormServiceTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process-form.bpmn20.xml"})
    public void testFormService() {
        FormService formService = activitiRule.getFormService();
        // 获取流程定义文件
        ProcessDefinition processDefinition = activitiRule.getRepositoryService().createProcessDefinitionQuery().singleResult();

        // 获取 startForm 的 key 和 data
        String startFormKey = formService.getStartFormKey(processDefinition.getId());
        log.info("startFormKey: {}", startFormKey);
        StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
        log.info("startFormKey: {}", ToStringBuilder.reflectionToString(startFormData, ToStringStyle.JSON_STYLE));
        for (FormProperty startFormProperty : startFormData.getFormProperties()) {
            log.info("startFormProperty: {}", ToStringBuilder.reflectionToString(startFormProperty, ToStringStyle.JSON_STYLE));
        }

        // 启动流程
        Map<String, String> properties = Maps.newHashMap();
        properties.put("message", "my test message");
        formService.submitStartFormData(processDefinition.getId(), properties);

        // 查询 task
        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();

        // 获取 taskForm 的 data
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        log.info("taskFormData: {}", ToStringBuilder.reflectionToString(taskFormData, ToStringStyle.JSON_STYLE));
        for (FormProperty taskFormProperty : taskFormData.getFormProperties()) {
            log.info("taskFormProperty: {}", ToStringBuilder.reflectionToString(taskFormProperty, ToStringStyle.JSON_STYLE));
        }

        HashMap<String, String> map = Maps.newHashMap();
        map.put("yesOrNo", "yes");
        formService.submitTaskFormData(task.getId(), map);

        Task task1 = activitiRule.getTaskService().createTaskQuery().taskId(task.getId()).singleResult();
        log.info("task1: {}", task1);
    }
}