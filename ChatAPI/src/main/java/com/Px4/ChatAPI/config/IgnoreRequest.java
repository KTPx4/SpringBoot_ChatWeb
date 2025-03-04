package com.Px4.ChatAPI.config;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class IgnoreRequest {
    @Getter
    private static List<String> ignoreList = Arrays.asList(
            "/ws",
            "/ws/**",
            "/avt/**",
            "/avt/",
            "/api/v1/upload",
            "/api/v1/file",
            "/api/v1/account/login",
            "/api/v1/account/register",
            "/api/v1/account/reset"
//            "/", "/{x:[\\w\\-]+}", "/{x:^(?!api$).*$}/*/{y:[\\w\\-]+}","/error"
    );

    public static boolean isIgnore(String requestPath)
    {
        return ignoreList.stream().anyMatch(requestPath::startsWith);
    }

}
