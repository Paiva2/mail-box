package com.root.mailbox.config;

import com.google.gson.Gson;
import com.root.mailbox.presentation.dto.mapper.JoinInboxMapper;
import com.root.mailbox.presentation.dto.mapper.NewMessageInboxMapper;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.*;

@Configuration
@RestController
public class WebsocketConnections implements ApplicationListener<ApplicationEvent> {
    private Boolean timerExecuted = false;

    private final SimpMessageSendingOperations messagingTemplate;
    private final HashMap<String, JoinInboxMapper> connectionsIds = new HashMap<>();

    public WebsocketConnections(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof SessionConnectedEvent) {
            handleSessionConnected((SessionConnectedEvent) event);
        } else if (event instanceof SessionDisconnectEvent) {
            handleSessionDisconnect((SessionDisconnectEvent) event);
        }

        if (!timerExecuted) {
            showConnectionsTimer();
            timerExecuted = true;
        }
    }

    private void showConnectionsTimer() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println(("Connections: ".concat(connectionsIds.toString())));
            }
        }, 0, 15000);
    }

    private void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        StompHeaderAccessor accessHeader = MessageHeaderAccessor.getAccessor(
            (Message<?>) headerAccessor.getHeader("simpConnectMessage"),
            StompHeaderAccessor.class
        );

        String sessionId = headerAccessor.getSessionId();
        String joinInbox = accessHeader.getNativeHeader("WS_USER").get(0);

        Gson gson = new Gson();

        connectionsIds.put(sessionId, gson.fromJson(joinInbox, JoinInboxMapper.class));

        System.out.println("New connection: " + sessionId);
    }

    private void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        connectionsIds.remove(sessionId);

        System.out.println("Sessão desconectada: " + sessionId);
    }

    @MessageMapping("/inbox")
    public void handleSendEmail(@Payload NewMessageInboxMapper message, SimpMessageHeaderAccessor accessor) {
        List<String> sessionsToReceive = connectionsIds.entrySet()
            .stream()
            .filter(entry -> message.getEmailPayload().getToEmails().contains(entry.getValue().getEmail()) || message.getEmailPayload().getCopyList().contains(entry.getValue().getEmail()))
            .map(Map.Entry::getKey)
            .toList();

        sessionsToReceive.forEach(session -> {
            messagingTemplate.convertAndSend(
                "/queue/inbox/".concat(connectionsIds.get(session).getEmail()),
                message.getEmailPayload()
            );
        });
    }
}
