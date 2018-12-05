package org.destiny.activiti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.destiny.activiti.model.ActivitiModel;
import org.destiny.activiti.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/processDef")
@Api(
        value = "/processDef",
        description = "流程定义"
)
@Slf4j
public class ProcessDefinitionController {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 创建流程定义:
     * 把跳转到 activiti-modeler 流程图设计界面的路径返回到了前端, 然后让前端在跳转到流程图设计界面
     *
     * @param activitiModel
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/create.do", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "创建流程定义", response = Result.class)
    @ApiResponses({
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 500, message = "server error")
    })
    public Result create(@RequestBody @ApiParam(name = "activitiModel", required = true, value = "流程定义实体") ActivitiModel activitiModel,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        Map<String, Object> map = Maps.newHashMap();
        try {
            Model model = repositoryService.newModel();
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put(ModelDataJsonConstants.MODEL_NAME, activitiModel.getName());
            objectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            objectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, StringUtils.defaultString(activitiModel.getDescription()));
            model.setMetaInfo(objectNode.toString());
            model.setName(activitiModel.getName());
            model.setKey(StringUtils.defaultString(activitiModel.getKey()));
            repositoryService.saveModel(model);
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id", "destiny");
            editorNode.put("resourceId", "destiny");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.put("stencilset", stencilSetNode);
            repositoryService.addModelEditorSource(model.getId(), editorNode.toString().getBytes("utf-8"));

            map.put("modelId", model.getId());
            return Result.genSuccess(map);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            return Result.genFail(500, e.getMessage());
        }
    }


    /**
     * 流程模型定义列表展示
     * @param request
     * @return
     */
    @PostMapping(value = "/list", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "查询流程定义列表", response = Result.class)
    public Result list(HttpServletRequest request) {
//        List<ActivitiModel> modelList = Lists.newArrayList();
        List<Model> modelList = repositoryService.createModelQuery().list();
        if (!CollectionUtils.isEmpty(modelList)) {
            return Result.genSuccess(modelList);
        } else {
            return Result.genFail(500, "流程定义文件为空");
        }
    }

    /**
     * 根据模型 id 部署流程定义
     * 与流程定义相关的有三张表:
     *      - ACT_GE_BYTEARRAY
     *      - ACT_RE_PROCDEF
     *      - ACT_RE_DEPLOYMENT
     * 根据前段传入的 deploymentId 部署流程定义, 根据 deploymentId 查询出创建模型是生成的相关文件, 然后进行一定的转换后进行部署
     *
     * @param activitiModel
     * @return
     */
    @PostMapping(value = "/deploy")
    public Result deploy(@RequestBody ActivitiModel activitiModel) throws IOException {
        String modelId = activitiModel.getId();
        Model model = repositoryService.getModel(modelId);
        ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(model.getId()));
        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);

        String processName = model.getName() + ".bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment()
                .name(model.getName())
                .addString(processName, new String(bpmnBytes))
                .deploy();
        if (deployment != null && deployment.getId() != null) {
            return Result.genSuccess(deployment);
        } else {
            return Result.genFail(500, "deployment 对象为空");
        }
    }
}
