package com.Px4.ChatAPI.controllers.socket;
import com.Px4.ChatAPI.controllers.requestParams.chat.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.io.IOException;
import java.util.*;

@Component
public class WebSocketEventListener   {

    private final SimpMessagingTemplate messagingTemplate;

    private static final Set<UserSession> LIST_SESSION = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    public void addSession(String userId, String session) {
        LIST_SESSION.add(new UserSession(userId, session));
    }

    public void removeSession(String session) {
        LIST_SESSION.removeIf(userSession -> userSession.getSession().equals(session));
    }

    private String getUserSessionsAsJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        // Chuyển đổi Set<UserSession> thành List<UserSession>
        List<UserSession> userSessionsList = new ArrayList<>(LIST_SESSION);
        // Chuyển đổi danh sách sang JSON
        return objectMapper.writeValueAsString(userSessionsList);
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {

        try {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());


            if(headerAccessor.getSessionAttributes() != null && headerAccessor.getSessionAttributes().get("id") != null)
            {
                // Lấy id từ session attributes
                String id = (String) headerAccessor.getSessionAttributes().get("id");

                String idSession = headerAccessor.getSessionId();
                if(id != null && !id.isEmpty() && idSession != null && !idSession.isEmpty())
                {
                    addSession(id, idSession);
                }

                String json = getUserSessionsAsJson();
                // Gửi JSON qua WebSocket hoặc REST API
                // Ví dụ: sendToWebSocket(json);

                MessageResponse messRes = new MessageResponse("online", "server", "all", json, "json");

                messagingTemplate.convertAndSend("/list/online", messRes);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }



      //  System.out.println("Received a new WebSocket connection: " + id +"-session: " + idSession);


    }

    @EventListener
    public void handleWebSocketConnectedListener(SessionConnectedEvent event) {

        try {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());


            if(headerAccessor.getSessionAttributes() != null && headerAccessor.getSessionAttributes().get("id") != null)
            {
                String json = getUserSessionsAsJson();
                // Gửi JSON qua WebSocket hoặc REST API
                // Ví dụ: sendToWebSocket(json);
              //  System.out.println(json);
                MessageResponse messRes = new MessageResponse("online", "server", "all", json, "json");

                messagingTemplate.convertAndSend("/list/online", messRes);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        try {

            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());


            if(headerAccessor.getSessionAttributes() != null && headerAccessor.getSessionAttributes().get("id") != null)
            {
                String id = (String) headerAccessor.getSessionAttributes().get("id");

                String idSession = headerAccessor.getSessionId();
                if(id != null && !id.isEmpty() && idSession != null && !idSession.isEmpty())
                {
                    removeSession(idSession);
                }

                String json = getUserSessionsAsJson();

                MessageResponse messRes = new MessageResponse("online", "server", "all", json, "json");

                messagingTemplate.convertAndSend("/list/online", messRes);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public class UserSession {
        private final String userId;
        private final String session;

        public UserSession(String userId, String session) {
            this.userId = userId;
            this.session = session;
        }

        public String getUserId() {
            return userId;
        }

        public String getSession() {
            return session;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserSession that = (UserSession) o;
            return userId.equals(that.userId) && session.equals(that.session);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, session);
        }
    }

}

