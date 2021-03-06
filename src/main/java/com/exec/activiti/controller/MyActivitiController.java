package com.exec.activiti.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/models")
public class MyActivitiController {


    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping("/modelList")
    public String modelList(org.springframework.ui.Model model, HttpServletRequest request){
        log.info("-------------列表页-------------");
        List<Model> models = repositoryService.createModelQuery().orderByCreateTime().desc().list();
        model.addAttribute("models",models);
        String info = request.getParameter("info");
        if(StringUtils.isNotEmpty(info)){
            model.addAttribute("info",info);
        }
        return "model/list";
    }

    @RequestMapping("/create")
    public void newModel(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
       try {
           //初始化一个空模型
           Model model = repositoryService.newModel();

           //设置一些默认信息
           String name = "new-process";
           String description = "";
           int revision = 1;
           String key = "process";

           ObjectNode modelNode = objectMapper.createObjectNode();
           modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
           modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
           modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);

           model.setName(name);
           model.setKey(key);
           model.setMetaInfo(modelNode.toString());

           repositoryService.saveModel(model);
           String id = model.getId();

           //完善ModelEditorSource
           ObjectNode editorNode = objectMapper.createObjectNode();
           editorNode.put("id", "canvas");
           editorNode.put("resourceId", "canvas");
           ObjectNode stencilSetNode = objectMapper.createObjectNode();
           stencilSetNode.put("namespace",
                   "http://b3mn.org/stencilset/bpmn2.0#");
           editorNode.put("stencilset", stencilSetNode);
           repositoryService.addModelEditorSource(id, editorNode.toString().getBytes("utf-8"));

           response.sendRedirect(request.getContextPath() + "/static/modeler.html?modelId=" + id);
       }catch (IOException e){
           e.printStackTrace();
           log.info("模型创建失败！");
       }

    }

    @RequestMapping("/delete/{id}")
    public @ResponseBody String deleteModel(@PathVariable("id")String id){
        repositoryService.deleteModel(id);
        return "删除成功！";
    }


    @RequestMapping("/deployment/{id}")
    public @ResponseBody String deploy(@PathVariable("id")String id) throws Exception {

        //获取模型
        Model modelData = repositoryService.getModel(id);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());

        if (bytes == null) {
            return "模型数据为空，请先设计流程并成功保存，再进行发布。";
        }

        JsonNode modelNode = new ObjectMapper().readTree(bytes);

        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        if(model.getProcesses().size()==0){
            return "数据模型不符要求，请至少设计一条主线流程。";
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);

        //发布流程
        String processName = modelData.getName() + ".bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment()
                .name(modelData.getName())
                .addString(processName, new String(bpmnBytes, "UTF-8"))
                .deploy();
        modelData.setDeploymentId(deployment.getId());
        repositoryService.saveModel(modelData);
        return "流程发布成功";
    }

    @RequestMapping("/start/{id}")
    public @ResponseBody String startProcess(@PathVariable("id") String id){
        try {


        }catch (Exception e){
            e.printStackTrace();
            return "流程启动失败！";
        }
        return "流程启动成功";
    }


}