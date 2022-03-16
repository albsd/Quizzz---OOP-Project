package client.utils;

import org.springframework.messaging.simp.stomp.StompSession.Subscription;

public interface WebSocketSubscription {
    Subscription[] registerForMessages();
} 
