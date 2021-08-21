package data.model;

import lombok.Data;
import service.Message;

import java.time.LocalDateTime;

import static data.model.RequestStatus.PENDING;

@Data
public class Request implements Message<Request> {
    private final String senderName;
    private final String senderId;
    private final String recipientId;
    private final LocalDateTime dateCreated;
    private final RequestStatus requestStatus = PENDING;

    public Request(String senderName, String senderId, String recipientId) {
        this.senderName = senderName;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.dateCreated = LocalDateTime.now();
    }

    public String toString(){
        int year = dateCreated.getYear();
        int month = dateCreated.getMonthValue();
        int day = dateCreated.getDayOfMonth();
        int hour = dateCreated.getHour();
        int minute = dateCreated.getMinute();
        return String.format("You have received a friend request from %s at %s", senderName, String.format("%d-%d-%d:%02d:%02d",year, month, day, hour, minute));
    }

}
