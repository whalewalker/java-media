package data.repository;

import data.model.ChatRoom;
import service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatRoomDatabaseImpl<T extends Storable> implements Database<T>{

    public List<T> chatRoomStore = new ArrayList<>();
    private static ChatRoomDatabaseImpl<ChatRoom> instance = null;


    public static ChatRoomDatabaseImpl<ChatRoom> getInstance() {
        if(instance == null){
            instance = new ChatRoomDatabaseImpl<>();
        }
        return instance;
    }

    @Override
    public void save(T t) {
        chatRoomStore.add(t);
    }

    @Override
    public boolean contain(T t) {
        return chatRoomStore.contains(t);
    }

    @Override
    public void delete(T t) {
        chatRoomStore.remove(t);
    }

    @Override
    public List<T> findAll() {
        return chatRoomStore;
    }

    @Override
    public int size() {
        return chatRoomStore.size();
    }

    @Override
    public Optional<T> findById(String id) {
        for (T t: chatRoomStore) {
            if (t.getId().equals(id)) return Optional.of(t);
        }
        return Optional.empty();
    }

    @Override
    public List<T> findAllByName(String name) {
       List<T> chatRooms = new ArrayList<>();
        for (T t: chatRoomStore) {
            if (t.getName().contains(name)) chatRooms.add(t);
        }
        return chatRooms;
    }

    @Override
    public void deleteAll() {
        chatRoomStore.clear();
    }
}
