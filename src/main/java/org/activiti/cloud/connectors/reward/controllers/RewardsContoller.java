package org.activiti.cloud.connectors.reward.controllers;

import java.util.List;

import org.activiti.cloud.connectors.reward.model.Reward;
import org.activiti.cloud.connectors.reward.services.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RewardsContoller {

    @Autowired
    private RewardService rewardService;

    @RequestMapping(method = RequestMethod.GET, path = "/")
    public String welcome() {
        return "Hello From the Trending Topic Campaigns: Rewards Connector Service";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rewards/{campaign}")
    public List<Reward> getRewardsByCampaign(@PathVariable("campaign") String campaign) {
        return rewardService.getRewardsByCampaign(campaign);
    }
}
