package client.utils;

import org.springframework.messaging.simp.stomp.StompSession.Subscription;

public interface WebSocketSubscription {
    /**
     * Registers the player to the server's messages for chat messages from other players
     * and disconnection updates in the chat and time halving updates.
     * @return the subscriptions of the client
     */
    Subscription[] registerForMessages();
} 
