package com.Px4.ChatAPI.models;

import java.time.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

// Lấy thời gian từ MongoDB (UTC)
public class ConverDateTime {

    public static String toHCMtime(Date date) {
        // Chuyển Date thành Instant
        Instant instant = date.toInstant();

        // Chuyển Instant sang ZonedDateTime với múi giờ Asia/Ho_Chi_Minh
        ZonedDateTime hcmTime = instant.atZone(ZoneId.of("Asia/Ho_Chi_Minh"));

        // Định dạng lại thời gian nếu cần
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Chuyển thành chuỗi với định dạng mong muốn
        System.out.println(hcmTime.format(formatter));
        return hcmTime.format(formatter);
    }

}
