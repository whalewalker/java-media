package data.repository;

import data.model.Request;
import service.Message;
import data.model.User;
import service.MessageDispatcher;

public class FriendRequestDispatcher implements MessageDispatcher {
    @Override
    public void send(User recipient, Message<Request> friendRequest) {
        recipient.updatePendingFriendRequest(friendRequest);
    }
}
