package data.repository;

import data.model.ChatRoom;
import data.model.User;
import web.exception.UserAuthException;

import java.util.*;

public class ChatRoomRepository<T extends ChatRoomStorable> implements Database<T>{

    private static ChatRoomRepository<ChatRoom> single_instance = null;
    private final List<T> userStore = new ArrayList<>();

    public static ChatRoomRepository<ChatRoom> getInstance() {
        if (single_instance ==  null){
            single_instance = new ChatRoomRepository<>();
        }
        return single_instance;
    }

    @Override
    public void save(T t) {

    }

    @Override
    public boolean contain(T t) {
        return false;
    }

    @Override
    public void delete(T t) {

    }

    @Override
    public List<T> findAll() {
        return null;
    }

    @Override
    public void checkEmail(String email) throws UserAuthException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Optional<T> findById(String id) {
        for (T t: userStore) {
            if (t.getId().equals(id)) return Optional.of(t);
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public List<T> findAllByName(String name) {
        return null;
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Optional<T> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void addEmail(String email) {

    }
}
