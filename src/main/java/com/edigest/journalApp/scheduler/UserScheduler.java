package com.edigest.journalApp.scheduler;

import com.edigest.journalApp.cache.AppCache;
import com.edigest.journalApp.entity.User;
import com.edigest.journalApp.enums.Sentiment;
import com.edigest.journalApp.repository.UserRepositoryImpl;
import com.edigest.journalApp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserScheduler {

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Autowired
    private EmailService emailService;

//    @Autowired
//    private AppCache appCache;

    
    @Scheduled(cron = "0 9 ? * SUN")
    public void fetchUsersAndSendSentimentAnalysisMail() {
        List<User> usersForSentimentAnalysis = userRepositoryImpl.getUsersForSentimentAnalysis();
        for(User user: usersForSentimentAnalysis) {
            List<Sentiment> filteredEntriesSentiments = user.getJournalEntries().stream().filter(entry -> entry.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(entry -> entry.getSentiment()).collect(Collectors.toList());
            Sentiment mostFrequentSentiment = getMostFrequentSentiment(filteredEntriesSentiments);
            if(mostFrequentSentiment != null) {
                emailService.sendEmail(user.getEmail(), "Sentiment for last 7 days", mostFrequentSentiment.toString());
            }
        }
    }

    private static Sentiment getMostFrequentSentiment(List<Sentiment> filteredEntriesSentiments) {
        Map<Sentiment, Integer> sentimentsCount = new HashMap<>();
        for(Sentiment sentiment : filteredEntriesSentiments) {
            if(sentiment != null) {
                sentimentsCount.put(sentiment, sentimentsCount.getOrDefault(sentiment, 0) + 1);
            }
        }

        Sentiment mostFrequentSentiment = null;
        int maxCount = 0;
        for(Map.Entry<Sentiment, Integer> entry: sentimentsCount.entrySet()) {
            if(entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequentSentiment = entry.getKey();
            }
        }
        return mostFrequentSentiment;
    }

//    @Scheduled(cron = "0/10 * ? * *")
//    public void clearAppCache() {
//        appCache.init();
//    }

}
