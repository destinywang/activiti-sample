package org.destiny.activiti;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.form.api.FormRepositoryService;
import org.activiti.form.api.FormService;
import org.junit.Test;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/31 23:30
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
public class ProcessEnginesTest {

    @Test
    public void testProcessEngine() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        log.info("processEngine: {}", processEngine);   // org.activiti.engine.impl.ProcessEngineImpl@66ea1466
        Class<? extends ProcessEngine> processEngineClass = processEngine.getClass();
        log.info("class: {}", processEngineClass);      // org.activiti.engine.impl.ProcessEngineImpl
        DynamicBpmnService dynamicBpmnService = processEngine.getDynamicBpmnService();
        FormService formEngineFormService = processEngine.getFormEngineFormService();
        FormRepositoryService formEngineRepositoryService = processEngine.getFormEngineRepositoryService();
        org.activiti.engine.FormService formService = processEngine.getFormService();
        HistoryService historyService = processEngine.getHistoryService();
        IdentityService identityService = processEngine.getIdentityService();
        ManagementService managementService = processEngine.getManagementService();
        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();  // 流程引擎配置类
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();                                      // 运行时
        TaskService taskService = processEngine.getTaskService();                                               // 任务相关

        log.info("dynamicBpmnService: {}", dynamicBpmnService);                     // org.activiti.engine.impl.DynamicBpmnServiceImpl@1601e47
        log.info("formEngineFormService: {}", formEngineFormService);               // null
        log.info("formEngineRepositoryService: {}", formEngineRepositoryService);   // null
        log.info("formService: {}", formService);                                   // org.activiti.engine.impl.FormServiceImpl@3bffddff
        log.info("historyService: {}", historyService);                             // org.activiti.engine.impl.HistoryServiceImpl@66971f6b
        log.info("identityService: {}", identityService);                           // org.activiti.engine.impl.IdentityServiceImpl@50687efb
        log.info("managementService: {}", managementService);                       // org.activiti.engine.impl.ManagementServiceImpl@517bd097
        log.info("processEngineConfiguration: {}", processEngineConfiguration);     // org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration@142eef62
        log.info("repositoryService: {}", repositoryService);                       // org.activiti.engine.impl.RepositoryServiceImpl@4a9cc6cb
        log.info("runtimeService: {}", runtimeService);                             // org.activiti.engine.impl.RuntimeServiceImpl@5990e6c5
        log.info("taskService: {}", taskService);                                   // org.activiti.engine.impl.TaskServiceImpl@56e07a08
    }

}
