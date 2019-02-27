package org.destiny.activiti.form;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.form.api.FormDeployment;
import org.activiti.form.api.FormRepositoryService;
import org.activiti.form.engine.FormEngine;
import org.activiti.form.engine.FormEngineConfiguration;
import org.activiti.form.engine.test.ActivitiFormRule;
import org.activiti.form.model.FormDefinition;
import org.activiti.form.model.FormField;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2018-12-13 14:36
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2018
 */
@Slf4j
public class FormTest {

    @Rule
    public ActivitiFormRule activitiFormRule = new ActivitiFormRule("activiti-mysql.cfg.xml");

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

    private FormEngine formEngine;

    @Autowired
    private FormRepositoryService formRepositoryService;

    @Test
    public void setUp() {

        // 创建表单
        FormDefinition formDefinition = new FormDefinition();
        FormField formField = new FormField();
        formField.setId("formField1");
        formField.setName("name");
        formField.setReadOnly(true);
        formField.setRequired(true);
        formField.setType("string");
        formField.setValue("value");

        formDefinition.setFields(Arrays.asList(formField));

        FormDeployment formDeployment = formRepositoryService.createDeployment()
                .addClasspathResource("org/destiny/activiti/my-process.bpmn20.xml")
                .name("test form")
                .tenantId("bytedance")
                .addFormDefinition("resourceName", formDefinition)
                .deploy();

        log.info("formDeployment: {}", ToStringBuilder.reflectionToString(formDeployment, ToStringStyle.JSON_STYLE));

//        activitiRule.getFormService().submitStartFormData();

//        activitiFormRule.getFormEngine().getFormService().storeSubmittedForm()

    }
}
