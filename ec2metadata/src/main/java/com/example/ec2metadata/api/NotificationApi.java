package com.example.ec2metadata.api;

import com.example.ec2metadata.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationApi {

    private static final Logger log = LoggerFactory.getLogger(NotificationApi.class);
    private final NotificationService notificationService;

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestParam String email) {
        log.info("subscribe:: {}", notificationService.subscribe(email));
        return ResponseEntity.ok("Subscription request received. Please check your email for confirmation.");
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestParam String email) {
        notificationService.unsubscribe(email);
        return ResponseEntity.ok("Unsubscription request received. You will no longer receive notifications.");
    }
}
