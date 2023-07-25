package com.nftheater.api.config;

import com.nftheater.api.utils.DateUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeConfiguration {

    @Bean
    public Clock clock() {
        return Clock.system(DateUtil.getTimeZone());
    }

}
