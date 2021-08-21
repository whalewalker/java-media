package data.model;

import data.repository.Storable;
import lombok.Data;
import service.Message;
import service.UserService;
import service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static data.model.RequestStatus.ACCEPTED;
import static data.model.RequestStatus.REJECTED;

@Data

public abstract class User implements Storable {
    private String firstName;
    private String lastName;
    private String email;
    private boolean isLoggedIn;
    private String id;

    private final List<Message<Request>> friendRequests = new ArrayList<>();
    private final  List<String> friends = new ArrayList<>();

    public String viewMessage(int messageIndex){
        return friendRequests.get(messageIndex).toString();
    }

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        isLoggedIn = true;
    }

    public void handleRequests(Message<Request> messageRequest, RequestStatus requestStatus){
        if (requestStatus.equals(ACCEPTED)){
            UserService userService = UserServiceImpl.getInstance();
            friends.add(messageRequest.getSenderId());
            userService.friendMatcher(messageRequest);
        }else if (requestStatus.equals(REJECTED)){
            friendRequests.remove(messageRequest);
        }
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
