package org.destiny.activiti.controller;

import io.swagger.annotations.Api;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.destiny.activiti.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/process")
@Api(value = "/process")
public class ProcessController {

    @Autowired
    private RepositoryService repositoryService;


    @PostMapping("/list")
    public Result processList() {
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().list();
        return Result.genSuccess(processDefinitionList);
    }

}
