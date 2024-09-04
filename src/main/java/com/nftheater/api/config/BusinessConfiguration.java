package com.nftheater.api.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

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
    @Value("${paysolution.service.url}")
    private String paySolutionServiceUrl;
    @Value("${paysolution.merchant.id}")
    private String paySolutionMerchantId;
    @Value("${paysolution.api.key}")
    private String paySolutionApiKey;
    @Value("${paysolution.secret.key}")
    private String paySolutionSecretKey;
    @Value("${paysolution.auth.key}")
    private String paySolutionAuthKey;
    @Value("${paysolution.merchant.name}")
    private String paySolutionMerchantName;
    @Value("${paysolution.expired.duration}")
    private Duration paySolutionExpiredDuration;
    @Value("${paysolution.postback.url}")
    private String paySolutionPostbackUrl;
    @Value("${paysolution.return.url}")
    private String paySolutionReturnUrl;
    @Value("${paysolution.payment.url}")
    private String paySolutionPaymentUrl;
}
