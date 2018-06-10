package org.activiti.cloud.connectors.reward.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.activiti.cloud.connectors.reward.configuration.RewardMessageChannels;
import org.activiti.cloud.connectors.reward.controllers.RewardsContoller;
import org.activiti.cloud.connectors.reward.model.Campaign;
import org.activiti.cloud.connectors.reward.model.Reward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static net.logstash.logback.marker.Markers.append;

@Service
@EnableBinding(RewardMessageChannels.class)
public class RewardService {

    @Value("${spring.application.name}")
    private String appName;

    private Logger logger = LoggerFactory.getLogger(RewardsContoller.class);


    @Value("${campaignCycle1.campaigns}")
    private String cycle1Campaigns;

    private Map<String, List<Reward>> rewardsRepository = new ConcurrentHashMap<>();

    @Autowired
    @Qualifier(value = RewardMessageChannels.REWARD_CHANNEL)
    private MessageChannel rewardProducer;

    public RewardService(){
    }

    public void addNewRewardToCampaing(String campaign,
                                       Reward reward) {

        if (rewardsRepository.get(campaign) == null) {
            rewardsRepository.put(campaign,
                                  new ArrayList<>());
        }
        List<Reward> rewards = rewardsRepository.get(campaign);
        rewards.add(reward);
    }

    public List<Reward> getRewardsByCampaign(String campaign,
                                             int amount) {
        List<Reward> rewards = rewardsRepository.get(campaign);
        if (rewards != null && rewards.size() > amount) {
            return Collections.unmodifiableList(rewards.subList(0,
                                                                amount));
        }
        return rewards;
    }

    @Scheduled(fixedRateString = "${campaignCycle1.milliseconds}")
    public void triggerRewardProcessForCycle1Campaigns() {
        if (cycle1Campaigns != null) {
            logger.info(append("service-name",
                               appName),
                        ">>> Scheduled Reward triggered for : " + cycle1Campaigns);
            sendMessageForCampaigns(cycle1Campaigns);
        }
    }

    public void sendMessageForCampaigns(String campaigns) {
        String[] campaignArray = campaigns.split(",");
        for (String campaign : campaignArray) {
            String[] splitCampaignString = campaign.split("-");
            String name = splitCampaignString[0];
            String lang = splitCampaignString[1];
            rewardProducer.send(MessageBuilder.withPayload(new Campaign(name,
                                                                        lang)).setHeader("lang",
                                                                                         lang).setHeader("campaign",
                                                                                                         name).build());
        }
    }

    public void cleanRewardsForCampaign(String campaign) {
        rewardsRepository.remove(campaign);
    }
}
