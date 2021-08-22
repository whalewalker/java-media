package service;

import data.model.User;

public interface MessageService {
    void dispatchMessage(User sender, User recipient, String message);
}
