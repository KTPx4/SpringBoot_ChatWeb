package com.Px4.ChatAPI.config;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class IgnoreRequest {
    @Getter
    private static List<String> ignoreList = Arrays.asList(
            "/ws",
            "/ws/**",
            "/api/v1/account/login",
            "/api/v1/account/register",
            "/api/v1/account/reset"

    );

    public static boolean isIgnore(String requestPath)
    {
        return ignoreList.stream().anyMatch(requestPath::startsWith);
    }

}
