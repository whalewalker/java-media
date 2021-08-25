package service;

import data.model.ChatMessage;
import data.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageServiceImpl implements MessageService {

    public ChatMessage createNewChatMessage(String senderName, String senderId, String recipientId, String message) {
        return new ChatMessage(message, senderName, senderId, recipientId);
    }

    @Override
    public String dispatchMessage(User sender, User recipient, String message) {
        ChatMessage chatMessage = postMessage(sender, recipient, message);
        return chatMessage.getMessageId();
    }

    private ChatMessage postMessage(User sender, User recipient, String messageBody) {
        ChatMessage chatMessage = createNewChatMessage(sender.getName(), sender.getId(), recipient.getId(), messageBody);

        Map<String, List<Map<String, ChatMessage>>> recipientInbox = recipient.getInbox();
        Map<String, List<Map<String, ChatMessage>>> senderOutbox = sender.getOutbox();

        Map<String, ChatMessage> message = new HashMap<>();
        message.put(chatMessage.getMessageId(), chatMessage);

        if (recipientInbox.containsKey(sender.getId())){
            recipientInbox.get(sender.getId()).add(message);
        }else {
            recipientInbox.put(sender.getId(), List.of(message));
        }

        if (senderOutbox.containsKey(recipient.getId())){
            senderOutbox.get(recipient.getId()).add(message);
        }else {
            senderOutbox.put(recipient.getId(), List.of(message));
        }

        return chatMessage;
    }

    @Override
    public String dispatchMessage(User sender, User recipient, String messageBody, String messageId) {
        ChatMessage chatMessage = postMessage(sender, recipient, messageBody);

        sender.getOutbox().get(recipient.getId()).forEach(message -> {
            if (message.containsKey(chatMessage.getMessageId())){
                message.get(chatMessage.getMessageId()).linkMessage(messageId);
            }
        });

        recipient.getInbox().get(sender.getId()).forEach(message -> {
            if (message.containsKey(chatMessage.getMessageId())){
                message.get(chatMessage.getMessageId()).linkMessage(messageId);
            }
        });

        return chatMessage.getMessageId();
    }
}
