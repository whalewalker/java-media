package data.model;

import data.repository.ChatRoomStorable;
import lombok.Data;
import service.Message;
import web.exception.ChatRoomException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class ChatRoom implements Observable, ChatRoomStorable {
    private final Set<String> members;
    private final String groupId;
    private final Set<String> admins;
    private String groupName;

    public ChatRoom(String adminId, String groupName, String... members) {
        this.members = new HashSet<>();
        this.members.addAll(Arrays.asList(members));
        this.groupId = UUID.randomUUID().toString();
        this.admins = new HashSet<>();
        this.admins.add(adminId);
        this.groupName = groupName;
    }

    @Override
    public void subscribe(String... subscribersId) {
        members.addAll(Arrays.asList(subscribersId));
    }

    @Override
    public void removeObserver(String subscribersId) {
        members.remove(subscribersId);
    }

    @Override
    public void broadcast(Message<ChatMessage> message) {

    }

    public void addAdmin(String id) throws ChatRoomException {
        if (admins.size() == 3) throw new ChatRoomException("Can not add more than three admins");
        admins.add(id);
    }

    public  void removeAdmin(String id) throws ChatRoomException {
        if (admins.size() == 1) throw new ChatRoomException("Chat room must have at least one admin");
        admins.remove(id);
    }
}
