package data.model;

import data.repository.Storable;
import lombok.Data;
import service.Message;
import service.UserService;
import service.UserServiceImpl;
import web.exception.FriendRequestException;
import web.exception.UserAuthException;

import java.util.*;

import static data.model.RequestStatus.ACCEPTED;
import static data.model.RequestStatus.REJECTED;
import static java.lang.String.format;

@Data

public abstract class User implements Storable {
    public String password;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isLoggedIn;
    private String id;
    private final List<Message<Request>> friendRequests;
    private final Set<String> friends;
    private String profile;
    private Map<String, List<Map<String, ChatMessage>>> inbox = new HashMap<>();
    private Map<String, List<Map<String, ChatMessage>>> outbox = new HashMap<>();
    private List<String> chartRooms = new ArrayList<>();

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        isLoggedIn = true;
        friendRequests = new ArrayList<>();
        friends = new HashSet<>();
        profile = format( """
               First name: %s
               Last name: %s
               Email: %s
                """, firstName, lastName, email);
    }

    public String viewMessage(int messageIndex){
        return friendRequests.get(messageIndex).toString();
    }

    public void requestHandler(Message<Request> messageRequest, RequestStatus requestStatus) throws FriendRequestException {
        if (requestStatus.equals(ACCEPTED)){
            acceptRequest(messageRequest);
        }else if (requestStatus.equals(REJECTED)){
            rejectRequest(messageRequest);
        }
    }

    public void rejectRequest(Message<Request> messageRequest) throws FriendRequestException {
            friendRequests.remove(messageRequest);
    }
    public void acceptRequest(Message<Request> messageRequest) throws FriendRequestException {
            UserService userService = UserServiceImpl.getInstance();
            friends.add(messageRequest.getSenderId());
            userService.friendMatcher(messageRequest);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return firstName + " " + lastName;
    }

    public void updatePendingFriendRequest(Message<Request> message){
        friendRequests.add(message);
    }
}
