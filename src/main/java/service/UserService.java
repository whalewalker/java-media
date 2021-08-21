package service;

import data.dto.NativeDto;
import data.model.Request;
import data.model.User;
import web.exception.FriendRequestException;
import web.exception.UserNotFoundException;

import java.util.List;

public interface UserService {
    User registerNative(NativeDto nativeDto);
    List<User> getUsersByName(String name) throws UserNotFoundException;
    void sendFriendRequest(String senderId, String recipientId) throws FriendRequestException;
    void friendMatcher(Message<Request> requestMessages);
}
