package com.hoanghiep.hust.configuration;

import com.hoanghiep.hust.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration
@EnableScheduling
public class ScheduleTaskConfiguration {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Scheduled(cron = "0 0 6 * * *")
    public void clearMessages() {
        System.out.println(new Date());
        chatMessageRepository.deleteAll();
    }
}
