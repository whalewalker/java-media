package service;

import data.model.User;

public interface MessageService {
    String dispatchMessage(User sender, User recipient, String message);
    String dispatchMessage(User sender, User recipient, String message, String messageId);
}
