package data.model;

import service.UserService;
import service.UserServiceImpl;
import web.exception.UserAuthException;

import java.util.UUID;

public class Native extends User{
    private final String id = UUID.randomUUID().toString();
    private final UserServiceImpl userService = UserServiceImpl.getInstance();

    public Native(String firstName, String lastName, String email, String password){
        super(firstName, lastName, email, password);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return getFirstName() + " " + getLastName();
    }

    @Override
    public void logout() throws UserAuthException {
        if (!isLoggedIn()) throw new UserAuthException("You're already logged out");
        this.setLoggedIn(false);
    }

    @Override
    public void login(String email, String password) throws UserAuthException {
        if (isLoggedIn()) throw new UserAuthException("You're already logged in");
        if (userService.isValidUser(email, password)){
            this.setLoggedIn(true);
        }
    }
}
