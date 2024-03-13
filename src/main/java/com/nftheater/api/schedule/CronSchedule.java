package com.nftheater.api.schedule;

import com.nftheater.api.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class CronSchedule {

    private final EventService eventService;

    @Scheduled(cron = "0 0 10 1 * *")
    public void cronScheduleClearRegisterEvent() {
        log.info("Schedule ClearRegisterEvent tasks executed - {}", ZonedDateTime.now());
        eventService.clearAllRegister();
    }

}
