package service;

import data.dto.NativeDto;
import data.model.Request;
import data.model.User;
import data.repository.Database;
import data.repository.FriendRequestDispatcher;
import data.repository.UserDatabaseImpl;
import lombok.Getter;
import web.exception.FriendRequestException;
import web.exception.UnSupportedActionException;
import web.exception.UserAuthException;
import web.exception.UserNotFoundException;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class UserServiceImpl implements UserService{
    @Getter
    private final UserDatabaseImpl<User> userDatabase;
    private static UserServiceImpl instance = null;
    private final MessageService messageService;

    public UserServiceImpl() {
        this.userDatabase = UserDatabaseImpl.getInstance();
        messageService = new MessageServiceImpl();
    }

    @Override
    public User registerNative(NativeDto nativeDto) throws UserAuthException {
//        userDatabase.checkEmail(nativeDto.getEmail());
//        userDatabase.addEmail(nativeDto.getEmail());
        User user = NativeDto.unpack(nativeDto);
        user.setLoggedIn(true);
        userDatabase.save(user);
        return user;
    }

    @Override
    public List<User> getUsersByName(String name) throws UserNotFoundException {
        List<User> users = userDatabase.findAllByName(name);
        if (users.isEmpty()) throw new UserNotFoundException(format("No User found with %s", name));
        return users;
    }



    @Override
    public void sendFriendRequest(String senderId, String recipientId) throws FriendRequestException {
        User sender = userDatabase.findById(senderId).orElseThrow(()-> new FriendRequestException("Friend request sender does not exist"));
        String senderName = sender.getName();
        User recipient = userDatabase.findById(recipientId).orElseThrow(()-> new FriendRequestException("Friend request recipient does not exist"));
        Request request = new Request(senderName, senderId, recipientId);
        sendFriendRequest(request, recipient);
    }

    @Override
    public void friendMatcher(Message<Request> requestMessages) throws FriendRequestException {
        User sender = userDatabase.findById(requestMessages.getSenderId()).orElseThrow(()-> new FriendRequestException("Friend request sender does not exist"));
        sender.getFriends().add(requestMessages.getRecipientId());
    }

    @Override
    public void sendFriendRequest(Message<Request> requestMessage, User user) {
        FriendRequestDispatcher friendRequestDispatcher = new FriendRequestDispatcher();
        friendRequestDispatcher.send(user, requestMessage);
    }

    public static UserServiceImpl getInstance(){
        if (instance == null){
            instance = new UserServiceImpl();
        }
        return instance;
    }



    @Override
    public void login(String email, String password) throws UserAuthException {
        User user = userDatabase.findByEmail(email).orElseThrow(() -> new UserAuthException("Invalid user details"));
        if (user.getPassword().equals(password)) user.setLoggedIn(true);
        else throw new UserAuthException("Invalid user details");
    }

    @Override
    public void logout(User user) throws UserAuthException {
        if (!user.isLoggedIn()) throw new UserAuthException("User already logout");
        user.setLoggedIn(false);
    }

    @Override
    public String sendChatMessage(String senderId, String recipientId, String message) throws UserNotFoundException, UnSupportedActionException {
        User sender = userDatabase.findById(senderId).orElseThrow(() -> new UserNotFoundException("sender with id not found"));
        User recipient = userDatabase.findById(recipientId).orElseThrow(() -> new UserNotFoundException("recipient with id not found"));

        if (!recipient.getFriends().contains(senderId)) throw new UnSupportedActionException("Cannot send message to recipient who is not a friend");
        return messageService.dispatchMessage(sender, recipient, message);
    }

    @Override
    public String sendChatMessage(String senderId, String recipientId, String message, String messageId) throws UserNotFoundException, UnSupportedActionException {
        User sender = userDatabase.findById(senderId).orElseThrow(() -> new UserNotFoundException("sender with id not found"));
        User recipient = userDatabase.findById(recipientId).orElseThrow(() -> new UserNotFoundException("recipient with id not found"));

        if (!recipient.getFriends().contains(senderId)) throw new UnSupportedActionException("Cannot send message to recipient who is not a friend");
        return messageService.dispatchMessage(sender, recipient, message, messageId);
    }

}