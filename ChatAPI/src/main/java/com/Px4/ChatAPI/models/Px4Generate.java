package com.Px4.ChatAPI.models;

import com.Px4.ChatAPI.models.message.MessageModel;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

// Lấy thời gian từ MongoDB (UTC)
public class Px4Generate {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String toHCMtime(Date date) {
        // Chuyển Date thành Instant
        Instant instant = date.toInstant();

        // Chuyển Instant sang ZonedDateTime với múi giờ Asia/Ho_Chi_Minh
        ZonedDateTime hcmTime = instant.atZone(ZoneId.of("Asia/Ho_Chi_Minh"));

        // Định dạng lại thời gian nếu cần
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Chuyển thành chuỗi với định dạng mong muốn
//        System.out.println(hcmTime.format(formatter));
        return hcmTime.format(formatter);
    }

    // Hàm sắp xếp danh sách MessageModel theo createdAt
    public static void sortMessagesByDate(List<MessageModel> messages) {
        messages.sort(new Comparator<MessageModel>() {
            @Override
            public int compare(MessageModel m1, MessageModel m2) {
                return m1.getCreatedAt().compareTo(m2.getCreatedAt()); // Sắp xếp từ cũ nhất đến mới nhất
            }
        });
    }

    public static String generateChar(int length)
    {
        String token = "";
        StringBuilder tokenBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            tokenBuilder.append(CHARACTERS.charAt(index));
        }
        token = tokenBuilder.toString();
        return token;
    }

}
