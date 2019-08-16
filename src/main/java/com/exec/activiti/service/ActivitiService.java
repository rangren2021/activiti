package com.exec.activiti.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.task.Task;

import java.util.List;

public interface ActivitiService {

    void startProcesses(String id, String business_key);

    List<Task> findTasksByUserId(String userId);

    Task findTaskById(String taskId);

    void completeTask(String taskId, String userId, String result);

    void updateBizStatus(DelegateExecution execution, String status);

    //流程节点权限用户列表${ActivityDemoServiceImpl.findUsers(execution,sign)}
    List<String> findUsersForSL(DelegateExecution execution);

    //流程节点权限用户列表${ActivityDemoServiceImpl.findUsers(execution,sign)}
    List<String> findUsersForSP(DelegateExecution execution);

    void queryProImg(String processInstanceId) throws Exception;

    String queryProHighLighted(String processInstanceId) throws Exception;
}
