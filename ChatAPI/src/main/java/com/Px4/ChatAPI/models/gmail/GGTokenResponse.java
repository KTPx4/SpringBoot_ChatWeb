package com.Px4.ChatAPI.models.gmail;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.util.Key;

public class GGTokenResponse  extends TokenResponse {
    @Key("expires_in")
    private Integer expiresInSeconds;
}
