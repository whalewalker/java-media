package data.model;

import service.Message;

public interface Observable {
    void subscribe(String... subscribersId);
    void removeObserver(String subscribersId);
    void broadcast(Message<ChatMessage> message);
}
