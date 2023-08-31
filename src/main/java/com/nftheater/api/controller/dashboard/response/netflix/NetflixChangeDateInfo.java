package com.nftheater.api.controller.dashboard.response.netflix;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetflixChangeDateInfo {
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
