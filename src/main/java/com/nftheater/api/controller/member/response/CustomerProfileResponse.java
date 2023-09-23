package com.nftheater.api.controller.member.response;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class CustomerProfileResponse {
    private String userId;
    private String password;
    private String customerName;
    private String email;
    private String phoneNumber;
    private String verifiedStatus;
    private ZonedDateTime expiredDate;
    private long netflixDayLeft;
    private String netflixPackageName;
    private String netflixEmail;
    private String netflixPassword;
    private String youtubePackageName;
    private long youtubeDayLeft;
    private int memberPoint;
}
