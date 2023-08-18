package com.nftheater.api.utils;

import com.nftheater.api.controller.customer.response.CustomerResponse;
import lombok.experimental.UtilityClass;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class BusinessUtils {

    public static String getColor(String acctStatus) {
        if (acctStatus.equalsIgnoreCase("กำลังใช้งาน")) {
            return "#FF0000";
        } else if (acctStatus.equalsIgnoreCase("Admin")) {
            return "#FF0000";
        }else if (acctStatus.contains("รอ")) {
            return "#FFC100";
        } else {
            return "#008000";
        }
    }

    public static String getAccountStatus(CustomerResponse addAcc) {
        if (addAcc == null) {
            return "ว่าง";
        }
        if (addAcc.getDayLeft() > 3) {
            return "ไม่ว่าง";
        }
        return "รอ";
    }

}
