package com.nftheater.api.controller.dashboard.response.youtube;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YoutubeChangeDateInfo {
    private String changeDateToday;
    private int countToday;
    private String changeDateTomorrow;
    private int countTomorrow;
    private String changeDateDayPlusTwo;
    private int countDayPlusTwo;
    private String changeDateDayPlusThree;
    private int countDayPlusThree;
    private int totalAccount;
}
