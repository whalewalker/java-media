package service;

import data.model.ChatRoom;
import data.model.Request;
import data.model.User;
import data.repository.ChatRoomDatabaseImpl;
import data.repository.FriendRequestDispatcher;
import data.repository.UserDatabaseImpl;
import lombok.Data;
import web.exception.ChatRoomException;
import web.exception.FriendRequestException;

import java.util.Arrays;

import static java.lang.String.format;

@Data

public class ChatRoomServiceImpl implements ChatRoomService {
    private UserDatabaseImpl<User> userDatabase;
    private MessageService messageService;
    private ChatRoomDatabaseImpl<ChatRoom> chatRoomDatabase;
    private static ChatRoomService instance = null;

    public ChatRoomServiceImpl() {
        this.userDatabase = UserDatabaseImpl.getInstance();
        this.chatRoomDatabase = ChatRoomDatabaseImpl.getInstance();
        this.messageService = new MessageServiceImpl();
    }

    public static ChatRoomService getInstance(){
        if (instance == null){
            instance = new ChatRoomServiceImpl();
        }
        return instance;
    }

//    final static Logger logger = Logger.getLogger(ChatRoomDatabaseImpl.class.getName());

    @Override
    public void sendRequests(String groupId, String... members) throws ChatRoomException {
        ChatRoom chatRoom = chatRoomDatabase.findById(groupId).orElseThrow(() -> new ChatRoomException(format("Chatroom with %s id does not exist", groupId)));
        String groupName = chatRoom.getGroupName();

        Arrays.stream(members).forEach(memberId -> {
            try {
                User user = userDatabase.findById(memberId).orElseThrow(() -> new FriendRequestException("Friend request member does not exist"));
                Request request = new Request(groupName, groupId, memberId);
                sendRequest(request, user);
            } catch (FriendRequestException e) {
                e.printStackTrace();
            }
        });

    }

    private void sendRequest(Request request, User recipient) {
        FriendRequestDispatcher friendRequestDispatcher = new FriendRequestDispatcher();
        friendRequestDispatcher.send(recipient, request);
    }

    @Override
    public void registerChatRoom(ChatRoom chatRoom) throws ChatRoomException {
        chatRoomDatabase.save(chatRoom);
    }
}
