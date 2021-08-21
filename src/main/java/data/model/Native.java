package data.model;

import java.util.UUID;

public class Native extends User{
    private final String id = UUID.randomUUID().toString();

    public Native(String firstName, String lastName, String email){
        super(firstName, lastName, email);
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return getFirstName() + " " + getLastName();    }
}
