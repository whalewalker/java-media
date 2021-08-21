package service;

import data.dto.NativeDto;
import data.model.Request;
import data.model.User;
import data.repository.Database;
import data.repository.FriendRequestDispatcher;
import data.repository.UserDatabaseImpl;
import lombok.Getter;
import web.exception.FriendRequestException;
import web.exception.UserAuthException;
import web.exception.UserNotFoundException;

import java.util.List;

import static java.lang.String.format;

public class UserServiceImpl implements UserService{
    @Getter
    private final Database<User> userDatabase;
    private static UserServiceImpl instance = null;

    public UserServiceImpl() {
        this.userDatabase = UserDatabaseImpl.getInstance();
    }

    @Override
    public User registerNative(NativeDto nativeDto) {
        User user = NativeDto.unpack(nativeDto);
        user.setLoggedIn(true);
        userDatabase.save(user);
        return user;
    }

    @Override
    public List<User> getUsersByName(String name) throws UserNotFoundException {
        List<User> users = userDatabase.findByName(name);
        if (users == null) throw new UserNotFoundException(format("No User found with %s", name));
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

    public boolean isValidUser(String email, String password) throws UserAuthException {
       User user = userDatabase.findByEmail(email).orElseThrow(() -> new UserAuthException("Invalid details"));
       if (user.getPassword().equals(password)) return true;
       else throw  new UserAuthException("Invalid details");
    }
}