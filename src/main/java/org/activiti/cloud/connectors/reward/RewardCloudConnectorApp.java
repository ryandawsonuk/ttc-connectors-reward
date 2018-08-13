package org.activiti.cloud.connectors.reward;

import org.activiti.cloud.connectors.starter.configuration.EnableActivitiCloudConnector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableActivitiCloudConnector
@EnableScheduling
public class RewardCloudConnectorApp {

    public static void main(String[] args) {
        SpringApplication.run(RewardCloudConnectorApp.class,
                              args);
    }
}
