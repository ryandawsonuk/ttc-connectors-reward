package org.activiti.cloud.connectors.reward.connectors;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.cloud.connectors.reward.configuration.RewardsConfiguration;
import org.activiti.cloud.connectors.reward.model.RankedAuthor;
import org.activiti.cloud.connectors.reward.model.Reward;
import org.activiti.cloud.connectors.reward.services.RewardService;
import org.activiti.cloud.connectors.starter.channels.IntegrationResultSender;
import org.activiti.cloud.connectors.starter.model.IntegrationRequestEvent;
import org.activiti.cloud.connectors.starter.model.IntegrationResultEvent;
import org.activiti.cloud.connectors.starter.model.IntegrationResultEventBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(RewardMessageChannels.class)
@RefreshScope
public class SendRewardConnector {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RewardService rewardService;

    private final IntegrationResultSender integrationResultSender;

    @Autowired
    private RewardsConfiguration rewardsConfiguration;

    public SendRewardConnector(IntegrationResultSender integrationResultSender) {
        this.integrationResultSender = integrationResultSender;
    }

    @StreamListener(value = RewardMessageChannels.REWARD_CONSUMER)
    public void rewardTopRankedUsers(IntegrationRequestEvent event) throws IOException {

        Collection winners = (Collection) event.getVariables().get("top");
        String campaign = String.valueOf(event.getVariables().get("campaign"));

        for (Object winner : winners) {
            RankedAuthor rankedAuthor = mapper.convertValue(winner,
                                                            RankedAuthor.class);

            Reward reward = new Reward(campaign,
                                       rankedAuthor,
                                       "You Won " + rankedAuthor.getUserName() + " for your participation in " + campaign,
                                       new Date());
            rewardService.addNewRewardToCampaing(campaign,
                                                 reward);
        }

        Map<String, Object> results = new HashMap<>();
        results.put("rewards",
                    rewardService.getRewardsByCampaign(campaign,
                                                       rewardsConfiguration.getAmount()));
        Message<IntegrationResultEvent> message = IntegrationResultEventBuilder.resultFor(event)
                .withVariables(results)
                .buildMessage();

        integrationResultSender.send(message);
    }
}
