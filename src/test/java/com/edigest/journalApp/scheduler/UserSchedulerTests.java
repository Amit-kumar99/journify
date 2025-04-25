package com.edigest.journalApp.scheduler;

import com.edigest.journalApp.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserSchedulerTests {

    @Autowired
    private UserScheduler userScheduler;

    @Test
    public void fetchUsersAndSendSentimentAnalysisMail() {
        userScheduler.fetchUsersAndSendSentimentAnalysisMail();
    }
}
