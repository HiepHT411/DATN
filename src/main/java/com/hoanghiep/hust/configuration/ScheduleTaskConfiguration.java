package com.hoanghiep.hust.configuration;

import com.hoanghiep.hust.repository.ChatMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduleTaskConfiguration {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Scheduled(cron = "0 0 6 * * *")
    public void clearMessages() {
        log.info("Deleted all messages at " + System.currentTimeMillis());
        chatMessageRepository.deleteAll();
    }
}
