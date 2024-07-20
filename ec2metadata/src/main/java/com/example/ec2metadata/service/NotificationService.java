package com.example.ec2metadata.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.Subscription;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final AmazonSNS snsClient;

    @Value("${aws.sns.topic.arn}")
    private String topicArn;

    public PublishResult subscribe(String email) {
        log.info("subscribe:: {}", email);
        snsClient.subscribe(topicArn, "email", email);
        return snsClient.publish(topicArn, "subscribed to notification");
    }

    public void unsubscribe(String email) {
        var subscriptions = snsClient.listSubscriptionsByTopic(topicArn);
        log.info("unsubscribe:: {}", email);
        for (Subscription subscription : subscriptions.getSubscriptions()) {
            if (subscription.getEndpoint().equals(email)) {
                snsClient.unsubscribe(subscription.getSubscriptionArn());
                break;
            }
        }
    }
}
