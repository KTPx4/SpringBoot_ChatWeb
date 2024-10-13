package com.Px4.ChatAPI.controllers.socket;

import com.Px4.ChatAPI.models.Message.MessageChat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MyWebSocketHandler extends TextWebSocketHandler {
    private ObjectMapper objectMapper = new ObjectMapper();

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Xử lý khi kết nối được thiết lập
        System.out.println("Kết nối mới: " + session.getId());
        sessions.add(session);
        MessageChat mess = new MessageChat("yourid", session.getId(), "you");
        String json = objectMapper.writeValueAsString(mess);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Xử lý khi nhận được tin nhắn
        System.out.println("Nhận tin nhắn: " + message.getPayload());

        MessageChat msg = objectMapper.readValue(message.getPayload(), MessageChat.class);

        String type = "", content= "", sender = "";
        sender  = msg.getSender();
        // Xử lý từng loại tin nhắn
        if ("chat".equals(msg.getType())) {
            // Gửi lại tin nhắn chat cho tất cả các client
            type = "chat";
            content = sender +": " + msg.getContent();

        }
        else if ("connect".equals(msg.getType()))
        {
            // Xử lý khi người dùng kết nối
            System.out.println("Người dùng kết nối: " + msg.getSender());
            type = "connect";
            content = "1 User join " + msg.getSender();

        }
        else if ("disconnect".equals(msg.getType()))
        {
            // Xử lý khi người dùng ngắt kết nối
            System.out.println("Người dùng ngắt kết nối: " + msg.getSender());
            type = "disconnect";
            content = "1 User disconnect " + msg.getSender();
        }

        MessageChat mess = new MessageChat(type,content, sender);
        String json = objectMapper.writeValueAsString(mess);

        for (WebSocketSession s : sessions) {
            s.sendMessage(new TextMessage(json));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Xử lý khi kết nối bị đóng
        System.out.println("Kết nối đóng: " + session.getId());
        String type = "disconnect";
        String content = "Server: 1 User disconnect ";
        String sender = "unknow";
        MessageChat mess = new MessageChat(type,content, sender);
        String json = objectMapper.writeValueAsString(mess);

        sessions.remove(session);
        // Gửi thông báo cho các người dùng còn lại
        for (WebSocketSession s : sessions) {
            s.sendMessage(new TextMessage(json));
        }
    }
}