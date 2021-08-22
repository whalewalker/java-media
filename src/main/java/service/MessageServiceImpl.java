package service;

import data.model.ChatMessage;
import data.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MessageServiceImpl implements MessageService{

    public ChatMessage createNewChatMessage(String senderName, String senderId, String recipientId, String message){
        return new ChatMessage(message, senderName, senderId, recipientId);
    }

    @Override
    public void dispatchMessage(User sender, User recipient, String message) {
        ChatMessage chatMessage = createNewChatMessage(sender.getName(), sender.getId(), recipient.getId(), message);

        Map<String, List<ChatMessage>> recipientInbox = recipient.getInbox();
        Map<String, List<ChatMessage>> senderOutbox = sender.getOutbox();

        if (recipientInbox.containsKey(sender.getId())) recipientInbox.get(sender.getId()).add(chatMessage);
        else recipientInbox.put(sender.getId(), List.of(chatMessage));

        if (senderOutbox.containsKey(recipient.getId())) senderOutbox.get(recipient.getId()).add(chatMessage);
        else senderOutbox.put(recipient.getId(), List.of(chatMessage));
    }
}
