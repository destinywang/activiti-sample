package org.destiny.activiti;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

@Slf4j
public class MyUnitTest {

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	@Deployment(resources = {"org/destiny/activiti/my-process.bpmn20.xml"})
	public void test() {
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
		log.info("processInstance.id: {}", processInstance.getId());
		log.info("processInstance.getProcessInstanceId: {}", processInstance.getProcessInstanceId());
		assertNotNull(processInstance);

		Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
		log.info("task.name: {}", task.getName());
		assertEquals("Activiti is awesome!", task.getName());
		activitiRule.getTaskService().createAttachment()
	}

}
