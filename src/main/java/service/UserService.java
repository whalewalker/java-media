package service;

import data.dto.NativeDto;
import data.model.Request;
import data.model.User;
import web.exception.FriendRequestException;
import web.exception.UserAuthException;
import web.exception.UserNotFoundException;

import java.util.List;

public interface UserService {
    User registerNative(NativeDto nativeDto) throws UserAuthException;
    List<User> getUsersByName(String name) throws UserNotFoundException;
    void sendFriendRequest(String senderId, String recipientId) throws FriendRequestException;
    void friendMatcher(Message<Request> requestMessages) throws FriendRequestException;
    void sendFriendRequest(Message<Request> requestMessage, User user);
    void login(String email, String password) throws UserAuthException;
    void logout(User user) throws UserAuthException;
    void sendChatMessage(String senderId, String recipientId, String message);
}
