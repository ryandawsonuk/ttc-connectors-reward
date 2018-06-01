package org.activiti.cloud.connectors.reward;

import org.activiti.cloud.connectors.starter.configuration.EnableActivitiCloudConnector;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableActivitiCloudConnector
@ComponentScan({"org.activiti.cloud.connectors.starter", "org.activiti.cloud.connectors.reward"})
@EnableScheduling
@RestController
public class RewardCloudConnectorApp implements CommandLineRunner {


    public static void main(String[] args) {
        SpringApplication.run(RewardCloudConnectorApp.class,
                              args);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/")
    public String welcome() {
        return "Welcome to the Campaign Engagement Rewards Service";
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
