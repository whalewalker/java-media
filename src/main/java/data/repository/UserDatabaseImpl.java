package data.repository;

import data.model.User;
import web.exception.UserAuthException;

import java.util.*;

public class UserDatabaseImpl<T extends Storable> implements Database<T>{
    private static UserDatabaseImpl<User> single_instance = null;

    private final Set<String> emails = new HashSet<>();
    List<T> userStore = new ArrayList<>();

    public static UserDatabaseImpl<User> getInstance() {
        if (single_instance ==  null){
            single_instance = new UserDatabaseImpl<>();
        }
        return single_instance;
    }

    @Override
    public void save(T t) {
        userStore.add(t);
    }

    @Override
    public boolean contain(T t) {
        return userStore.contains(t);
    }

    @Override
    public void delete(T t) {
        userStore.remove(t);
    }

    @Override
    public List<T> findAll() {
        return userStore;
    }

    public void checkEmail(String email) throws UserAuthException {
        if (emails.contains(email)) throw new UserAuthException("Email already exist");
    }

    @Override
    public int size() {
        return userStore.size();
    }

    @Override
    public Optional<T> findById(String id) {
        for (T t: userStore) {
            if (t.getId().equals(id)) return Optional.of(t);
        }
        return Optional.empty();
    }

    public Optional<T> findByUsername(String username) {
        for (T t : userStore) {
            if (t.getName().contains(username)) return Optional.of(t);
        }
        return Optional.empty();
    }

    @Override
    public List<T> findAllByName(String name) {
        List<T> listOfUsers = new ArrayList<>();
        for (T t : userStore){
            if (t.getName().contains(name)){
                listOfUsers.add(t);
            }
        }
        return listOfUsers;
    }

    @Override
    public void deleteAll() {
        userStore.clear();
    }
//
    public Optional<T> findByEmail(String email) {
        for (T t: userStore) {
            if (t.getEmail().equals(email)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    public void addEmail(String email) {
        emails.add(email);
    }


}
