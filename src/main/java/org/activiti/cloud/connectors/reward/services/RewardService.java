package org.activiti.cloud.connectors.reward.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.activiti.cloud.connectors.reward.model.Reward;
import org.springframework.stereotype.Service;

@Service
public class RewardService {

    private Map<String, List<Reward>> rewardsRepository = new ConcurrentHashMap<>();

    public RewardService() {
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

    public List<Reward> getRewardsByCampaign(String campaign) {
       return rewardsRepository.get(campaign);
    }
}
