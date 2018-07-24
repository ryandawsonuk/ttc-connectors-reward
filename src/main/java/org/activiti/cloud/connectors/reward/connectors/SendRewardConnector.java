package org.activiti.cloud.connectors.reward.connectors;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.cloud.connectors.reward.model.RankedAuthor;
import org.activiti.cloud.connectors.reward.model.Reward;
import org.activiti.cloud.connectors.reward.services.RewardService;
import org.activiti.cloud.connectors.starter.channels.IntegrationResultSender;
import org.activiti.cloud.connectors.starter.configuration.ConnectorProperties;
import org.activiti.cloud.connectors.starter.model.IntegrationResultBuilder;
import org.activiti.runtime.api.model.IntegrationRequest;
import org.activiti.runtime.api.model.IntegrationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(RewardMessageChannels.class)
public class SendRewardConnector {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RewardService rewardService;

    @Autowired
    private ConnectorProperties connectorProperties;

    private final IntegrationResultSender integrationResultSender;

    public SendRewardConnector(IntegrationResultSender integrationResultSender) {
        this.integrationResultSender = integrationResultSender;
    }

    @StreamListener(value = RewardMessageChannels.REWARD_CONSUMER)
    public void rewardTopRankedUsers(IntegrationRequest event) throws IOException {

        Collection winners = (Collection) event.getIntegrationContext().getInBoundVariables().get("top");
        String campaign = String.valueOf(event.getIntegrationContext().getInBoundVariables().get("campaign"));

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
                                                       5));
        Message<IntegrationResult> message = IntegrationResultBuilder.resultFor(event, connectorProperties)
                .withOutboundVariables(results)
                .buildMessage();

        integrationResultSender.send(message);
    }
}
