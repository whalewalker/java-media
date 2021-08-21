package service;

import data.model.Request;
import data.model.User;

public interface MessageDispatcher {
    void send(User recipient, Message<Request> message);
}
