package data.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.lang.String.format;

@Data
public class ChatMessage {
    private final String chatMessage;
    private final String senderName;
    private final String senderId;
    private final String recipientId;
    private final LocalDateTime timeSent;
    private final String messageId;
    private final Set<String> linkedMessages;

    public ChatMessage(String chatMessage, String senderName, String senderId, String recipientId) {
        this.chatMessage = chatMessage;
        this.senderName = senderName;
        this.senderId = senderId;
        this.recipientId = recipientId;
        timeSent = LocalDateTime.now();
        this.messageId = UUID.randomUUID().toString();
        this.linkedMessages = new HashSet<>();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd:HH/mm");
        return format("""
                Sender --> %s
                ----------------------
                Message --> %s     
                Time sent --> %s  
                -----------------------
                """, this.getSenderName(), this.getChatMessage(), formatter.format(this.getTimeSent()));

    }

    public void linkMessage(String messageId){
        linkedMessages.add(messageId);
    }
}
