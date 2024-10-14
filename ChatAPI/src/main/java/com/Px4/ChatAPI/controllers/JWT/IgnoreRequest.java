package com.Px4.ChatAPI.controllers.JWT;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IgnoreRequest {
    public static boolean isIgnore(String requestPath)
    {
        List<String> ignoreList = Arrays.asList(
                "/ws",
                "/api/account/login",
                "/api/account/register"
        );

        return ignoreList.contains(requestPath);
    }
}
