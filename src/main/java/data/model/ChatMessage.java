package data.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;

@Data
public class ChatMessage {
    private final String chatMessage;
    private final String senderName;
    private final String senderId;
    private final String recipientId;
    private final LocalDateTime timeSent;

    public ChatMessage(String chatMessage, String senderName, String senderId, String recipientId) {
        this.chatMessage = chatMessage;
        this.senderName = senderName;
        this.senderId = senderId;
        this.recipientId = recipientId;
        timeSent = LocalDateTime.now();
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
}
