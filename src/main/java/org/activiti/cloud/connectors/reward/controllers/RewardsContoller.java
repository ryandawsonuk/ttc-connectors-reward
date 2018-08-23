package org.activiti.cloud.connectors.reward.controllers;

import java.util.List;

import org.activiti.cloud.connectors.reward.model.Reward;
import org.activiti.cloud.connectors.reward.services.RewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static net.logstash.logback.marker.Markers.append;

@RequestMapping(path = "/v1")
@RestController
public class RewardsContoller {

    @Value("${spring.application.name}")
    private String appName;

    private Logger logger = LoggerFactory.getLogger(RewardsContoller.class);

    @Autowired
    private RewardService rewardService;

    @RequestMapping(method = RequestMethod.GET, path = "/")
    public String welcome() {
        return "{ \"welcome\" : \"Hello From the Trending Topic Campaigns: Rewards Connector Service\" }";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rewards/{campaign}")
    public List<Reward> getRewardsByCampaign(@PathVariable("campaign") String campaign) {
        return rewardService.getRewardsByCampaign(campaign,
                                                  5);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rewards/{campaign}")
    public void triggerRewardForCampaign(@PathVariable("campaign") String campaign) {
        logger.info(append("service-name",
                           appName),
                    ">>> Triggering Manual Reward For Campaign: " + campaign);
        rewardService.sendMessageForCampaigns(campaign);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/rewards/{campaign}")
    public void cleanRewardsForCampaign(@PathVariable("campaign") String campaign) {
        logger.info(append("service-name",
                           appName),
                    ">>> Cleaning Rewards For Campaign: " + campaign);
        rewardService.cleanRewardsForCampaign(campaign);
    }
}
