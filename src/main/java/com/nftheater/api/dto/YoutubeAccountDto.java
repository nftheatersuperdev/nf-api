package com.nftheater.api.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class YoutubeAccountDto {

    private UUID id;
    private String accountName;
    private String changeDate;
    private String youtubeEmail;
    private String youtubePassword;
    private String accountStatus;
    private ZonedDateTime expiredDate;
    private ZonedDateTime createdDate;
    private String createdBy;
    private ZonedDateTime updatedDate;
    private String updatedBy;
    private List<YoutubeLinkUserDto> accountLinks;
}
