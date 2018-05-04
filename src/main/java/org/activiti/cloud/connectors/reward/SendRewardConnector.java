package org.activiti.cloud.connectors.reward;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.cloud.connectors.reward.model.RankedAuthor;
import org.activiti.cloud.connectors.starter.channels.IntegrationResultSender;
import org.activiti.cloud.connectors.starter.model.IntegrationRequestEvent;
import org.activiti.cloud.connectors.starter.model.IntegrationResultEvent;
import org.activiti.cloud.connectors.starter.model.IntegrationResultEventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import static net.logstash.logback.marker.Markers.append;

@Component
@EnableBinding(RewardMessageChannels.class)
public class SendRewardConnector {

    private Logger logger = LoggerFactory.getLogger(SendRewardConnector.class);
    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    private ObjectMapper mapper;

    private final IntegrationResultSender integrationResultSender;

    public SendRewardConnector(IntegrationResultSender integrationResultSender) {
        this.integrationResultSender = integrationResultSender;
    }

    @StreamListener(value = RewardMessageChannels.REWARD_CONSUMER)
    public void tweet(IntegrationRequestEvent event) throws IOException {
        Map<String, Object> results = new HashMap<>();

        Collection winners = (Collection) event.getVariables().get("top");
        String campaign = String.valueOf(event.getVariables().get("campaign"));
        Map<String, String> rewardsText = new HashMap<>();

        for (Object winner : winners) {
            RankedAuthor rankedAuthor = mapper.convertValue(winner,
                                                            RankedAuthor.class);

            rewardsText.put(rankedAuthor.getUserName(),
                            "#  Campaign: " + campaign + " --> Reward time!!! You WON " + rankedAuthor.getUserName() + " with " + rankedAuthor.getNroOfTweets() + " tweets!!! ");
            logger.info(append("service-name",
                               appName),
                        "#" + campaign + "#################################################################");
            logger.info(append("service-name",
                               appName),
                        "Reward time!!! You WON " + winner + " with " + rankedAuthor.getNroOfTweets() + " tweets!!! ");
            logger.info(append("service-name",
                               appName),
                        "#################################################################################");
        }
        results.put("rewardsText",
                    rewardsText);
        Message<IntegrationResultEvent> message = IntegrationResultEventBuilder.resultFor(event)
                .withVariables(results)
                .buildMessage();

        integrationResultSender.send(message);
    }
}
