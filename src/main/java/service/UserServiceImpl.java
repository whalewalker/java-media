package service;

import data.dto.NativeDto;
import data.model.Request;
import data.model.User;
import data.repository.Database;
import data.repository.FriendRequestDispatcher;
import data.repository.UserDatabaseImpl;
import lombok.Data;
import lombok.Getter;
import web.exception.FriendRequestException;
import web.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

@Data
public class UserServiceImpl implements UserService{

    @Getter
    private final Database<User> userDatabase;

    private UserServiceImpl(){
        this.userDatabase = new UserDatabaseImpl<>();
    }


    @Override
    public User registerNative(NativeDto nativeDto) {
        User user = NativeDto.unpack(nativeDto);
        userDatabase.save(user);
        return user;
    }

    @Override
    public List<User> getUsersByName(String name) throws UserNotFoundException {
        List<User> users = userDatabase.findByName(name);
        if (users.isEmpty()) throw new UserNotFoundException(String.format("No user with %s name not found!", name));
        return users;
    }

    @Override
    public void sendFriendRequest(String senderId, String recipientId) throws FriendRequestException {
        Optional<User> sender = userDatabase.findById(senderId);
        if (sender.isPresent()){
            String senderName = sender.get().getName();
            Request request = new Request(senderName, senderId, recipientId);
            Optional<User> recipient = userDatabase.findById(recipientId);
            if (recipient.isPresent()) sendFriendRequest(request, recipient.get());
            else throw new FriendRequestException("Friend request with this recipient id does not exist!");
        }else throw new FriendRequestException("Friend request with this sender id does not exist!");
    }

    private void sendFriendRequest(Request request, User receiver) {
        FriendRequestDispatcher friendRequestDispatcher = new FriendRequestDispatcher();
        friendRequestDispatcher.send(receiver, (Message<Request>) request);
    }
    @Override
    public void friendMatcher(Message<Request> requestMessages) {
        User sender = userDatabase.findById(requestMessages.getSenderId()).get();
        sender.getFriends().add(requestMessages.getRecipientId());
    }


    private static class UserServiceSingletonHelper {
        private static final UserService instance = new UserServiceImpl();
    }

    public static UserService getInstance(){
        return UserServiceSingletonHelper.instance;
    }
}