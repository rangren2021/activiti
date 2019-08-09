package com.exec.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude=SecurityAutoConfiguration.class)
public class ActivitiApplicationTests {

    @Test
    public void contextLoads() {
        ProcessEngineConfiguration pec =    ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        pec.setJdbcUrl("jdbc:mysql://localhost:3306/activiti?characterEncoding=utf-8");
        pec.setJdbcDriver("com.mysql.jdbc.Driver");
        pec.setJdbcUsername("root");
        pec.setJdbcPassword("ft8441272");
//        设置自动生成表的属性是true, DB_SCHEMA_UPDATE_TRUE它的值就是true
        pec.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        ProcessEngine processEngine = pec.buildProcessEngine();


    }

}
