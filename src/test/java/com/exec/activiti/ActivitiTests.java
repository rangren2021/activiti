package com.exec.activiti;

import org.activiti.engine.*;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.task.Task;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class ActivitiTests {

    private static ProcessEngine processEngine = null;

    static {
//        ProcessEngine processEngine = ProcessEngineConfiguration
//                .createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();


        ProcessEngineConfiguration pec = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        pec.setJdbcUrl("jdbc:mysql://localhost:3306/activiti?characterEncoding=utf-8");
        pec.setJdbcDriver("com.mysql.jdbc.Driver");
        pec.setJdbcUsername("root");
        pec.setJdbcPassword("ft8441272");
//        设置自动生成表的属性是true, DB_SCHEMA_UPDATE_TRUE它的值就是true
        pec.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        processEngine = pec.buildProcessEngine();

    }

    /**
     * 发布流程
     * RepositoryService
     */
    @Test
    public void deployProcess() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.addClasspathResource("processes/Test.bpmn");
        builder.deploy();
    }

    /**
     * 启动流程
     * <p>
     * RuntimeService
     */
    @Test
    public void startProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //可根据id、key、message启动流程
        runtimeService.startProcessInstanceByKey("leave");
    }

    /**
     * 查看任务
     * TaskService
     */
    @Test
    public void queryTask() {
        TaskService taskService = processEngine.getTaskService(); //根据assignee(代理人)查询任务
        String assignee = "teacher";
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).list();
        int size = tasks.size();
        for (int i = 0; i < size; i++) {
            Task task = tasks.get(i);
        }
        for (Task task : tasks) {
            System.out.println("taskId:" + task.getId() + ",taskName:" + task.getName() + ",assignee:" + task.getAssignee() + ",createTime:" + task.getCreateTime());
        }
    }

    /**
     * 办理任务
     */
    @Test
    public void handleTask() {
        TaskService taskService = processEngine.getTaskService(); //根据上一步生成的taskId执行任务
        String taskId = "10005";
        taskService.complete(taskId);
    }


}
