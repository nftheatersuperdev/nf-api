package com.nftheater.api.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class BusinessConfiguration {

    @Value("${thai.bulk.sms.url}")
    private String smsUrl;
    @Value("${thai.bulk.sms.app.key}")
    private String smsAppKey;
    @Value("${thai.bulk.sms.app.secret}")
    private String smsAppSecret;
    @Value("${thai.bulk.sms.max.retry}")
    private Integer smsMaxRetry;
    @Value("${thai.bulk.sms.cool.down.time}")
    private Integer smsCoolDownTime;
    @Value("${line.bot.push.message.url}")
    private String lineUrl;
    @Value("${line.bot.channel-token}")
    private String lineToken;

}
