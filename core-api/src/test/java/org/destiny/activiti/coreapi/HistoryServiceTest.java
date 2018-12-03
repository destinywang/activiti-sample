package org.destiny.activiti.coreapi;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

@Slf4j
public class HistoryServiceTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti_history.cfg.xml");

    @Test
    @Deployment(resources = {"org/destiny/activiti/my-process-form.bpmn20.xmll"})
    public void testHistory() {
        HistoryService historyService = activitiRule.getHistoryService();

    }
}
