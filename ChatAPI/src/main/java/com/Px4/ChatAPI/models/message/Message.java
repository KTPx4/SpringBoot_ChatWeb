package com.Px4.ChatAPI.models.message;

import lombok.Builder;

@Builder
public record Message(String type, String content, String sender) { }
