import data.dto.NativeDto;
import data.model.ChatRoom;
import data.model.RequestStatus;
import data.model.User;
import data.repository.UserDatabaseImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserServiceImpl;
import web.exception.FriendRequestException;
import web.exception.UnSupportedActionException;
import web.exception.UserAuthException;
import web.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static data.model.RequestStatus.ACCEPTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;


public class SocialMediaTest{
    UserServiceImpl userServiceImpl;

    NativeDto Ismail;
    NativeDto Kabiru;
    NativeDto Mujibat;

    @BeforeEach
    void setUp(){
        userServiceImpl = UserServiceImpl.getInstance();
        Ismail = new NativeDto("Ismail", "Abdullah", "ismail@gmail.com", "12345");
        Kabiru = new NativeDto("Ismail", "Kabiru", "kabir@gmail.com", "12345");
        Mujibat = new NativeDto("Faruq", "Mujibat", "mujibat@gmail.com", "12345");
    }

    @AfterEach
    void tearDown(){
        userServiceImpl.getUserDatabase().deleteAll();
        userServiceImpl = null;
    }

    @Test
    void test_thatOnly_OneInstanceOfUserDataBaseIsCreatedTest(){
        UserDatabaseImpl<User> userDatabase = UserDatabaseImpl.getInstance();
        UserDatabaseImpl<User> userDatabase1 = UserDatabaseImpl.getInstance();
        assertThat(userDatabase1).isEqualTo(userDatabase);
    }


    @Test
    void canCreateANativeObject(){
        assertAll(
                ()-> assertEquals("Ismail", Ismail.getFirstName()),
                ()-> assertEquals("Abdullah", Ismail.getLastName()),
                ()-> assertEquals("ismail@gmail.com", Ismail.getEmail())
        );
    }

    @Test
    void native_canRegister(){
        User user = null;
        try {
            user = userServiceImpl.registerNative(Ismail);
        } catch (UserAuthException e) {
            e.printStackTrace();
        }
        assertTrue(userServiceImpl.getUserDatabase().contain(user));
        assertEquals(userServiceImpl.getUserDatabase().size(), 1);
    }

    @Test
    void UserIsAutomaticallyLoggedInWhenCreatedTest() throws UserAuthException {
        User user = userServiceImpl.registerNative(Ismail);
        assertThat(user.isLoggedIn()).isEqualTo(true);
    }

    @Test
    void UserCanLogoutTest() throws UserAuthException {
        User user = userServiceImpl.registerNative(Ismail);

        userServiceImpl.logout(user);
        assertThat(user.isLoggedIn()).isEqualTo(false);
    }

    @Test
    void UserCanLogInTest() throws UserAuthException {
        User user = userServiceImpl.registerNative(Kabiru);
        userServiceImpl.logout(user);

        userServiceImpl.login(Kabiru.getEmail(), Kabiru.getPassword());
        assertThat(user.isLoggedIn()).isEqualTo(true);
    }

    @Test
    void throwUserAuthExceptionWhenUserProvideInvalidDetailForLoginTest() throws UserAuthException {
        User user = userServiceImpl.registerNative(Kabiru);
        assertThatThrownBy(() -> userServiceImpl.login(user.getEmail(), "3456")).isInstanceOf(UserAuthException.class);
    }

    @Test
    void testThatUserCannotLogoutWhenAlreadyLoggedOut() throws UserAuthException {
        User user = userServiceImpl.registerNative(Kabiru);
        userServiceImpl.logout(user);
        assertThatThrownBy(()-> userServiceImpl.logout(user)).isInstanceOf(UserAuthException.class);
    }

    @Test
    void can_addMoreThanOne_native() throws UserAuthException {
        User user1 = userServiceImpl.registerNative(Ismail);
        User user2 = userServiceImpl.registerNative(Kabiru);

        assertTrue(userServiceImpl.getUserDatabase().contain(user1));
        assertTrue(userServiceImpl.getUserDatabase().contain(user2));

        assertEquals(userServiceImpl.getUserDatabase().size(), 2);
    }

    @Test
    void native_canFindOtherUserOnPlatform() throws UserNotFoundException, UserAuthException {
        User user1 = userServiceImpl.registerNative(Ismail);
        User user2 = userServiceImpl.registerNative(Kabiru);
        User user3 = userServiceImpl.registerNative(Mujibat);

        List<User> usersWithThatContainSearchName = userServiceImpl.getUsersByName("Ismail");
        assertAll(
                () -> assertTrue(usersWithThatContainSearchName.contains(user2)),
                () -> assertFalse(usersWithThatContainSearchName.contains(user3)),
                () -> assertTrue(usersWithThatContainSearchName.contains(user1)),
                () -> assertEquals(usersWithThatContainSearchName.size(), 2)
        );
    }

    @Test
    void applicationThrowException_whenUserWithSearchNameNotFound() throws UserAuthException {
        registerUser();
        assertThrows(UserNotFoundException.class, ()-> userServiceImpl.getUsersByName("whalewalker"));
    }

    private void registerUser() throws UserAuthException {
        User user1 = userServiceImpl.registerNative(Ismail);
        User user2 = userServiceImpl.registerNative(Kabiru);
        User user3 = userServiceImpl.registerNative(Mujibat);
    }

    @Test
    void userHaveUniqueUsername() throws UserAuthException {
        NativeDto nativeDto = new NativeDto("Native", "cool", "ismail@gmail.com", "2345");
        userServiceImpl.registerNative(Ismail);
        assertThrows(UserAuthException.class, ()-> userServiceImpl.registerNative(nativeDto));
    }

    @Test
    void nativeCan_sendFriendRequests_toAnotherNative() throws FriendRequestException, UserAuthException {
        User sender = userServiceImpl.registerNative(Ismail);
        User recipient = userServiceImpl.registerNative(Kabiru);

        userServiceImpl.sendFriendRequest(sender.getId(), recipient.getId());
        assertThat(recipient.getFriendRequests().size()).isEqualTo(1);

        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonthValue();
        int day = LocalDateTime.now().getDayOfMonth();
        int hour = LocalDateTime.now().getHour();
        int minute = LocalDateTime.now().getMinute();

        String requestMessage = String.format("You have received a friend request from Ismail Abdullah at %d-%d-%d:%02d:%02d", year, month, day, hour, minute);
        assertThat(recipient.viewMessage(0)).isEqualTo(requestMessage);
        System.out.println(recipient.viewMessage(0));
    }

    @Test
    void user_canAcceptFriendRequest_andAddFriendToFriendList() throws FriendRequestException, UserAuthException {
        User sender = userServiceImpl.registerNative(Ismail);
        User recipient = userServiceImpl.registerNative(Kabiru);

        userServiceImpl.sendFriendRequest(sender.getId(), recipient.getId());
        recipient.requestHandler(recipient.getFriendRequests().get(0), ACCEPTED);

        recipient.getFriends().forEach(System.out::println);

        assertThat(recipient.getId()).isIn(sender.getFriends());
        assertThat(sender.getId()).isIn(recipient.getFriends());
    }

    @Test
    void user_canRejectFriendRequest() throws FriendRequestException, UserAuthException {
        User sender = userServiceImpl.registerNative(Ismail);
        User recipient = userServiceImpl.registerNative(Kabiru);

        userServiceImpl.sendFriendRequest(sender.getId(), recipient.getId());
        recipient.requestHandler(recipient.getFriendRequests().get(0), RequestStatus.REJECTED);

        assertThat(recipient.getFriends()).doesNotContain(sender.getId());
        assertThat(recipient.getFriends()).isEmpty();
    }

    @Test
    void userCanSendMessageTest() throws UserAuthException, FriendRequestException, UserNotFoundException, UnSupportedActionException {
        User sender = userServiceImpl.registerNative(Ismail);
        User recipient = userServiceImpl.registerNative(Kabiru);

        userServiceImpl.sendFriendRequest(sender.getId(), recipient.getId());
        recipient.requestHandler(recipient.getFriendRequests().get(0), ACCEPTED);
        userServiceImpl.sendChatMessage(sender.getId(), recipient.getId(), "Hello buddy");
        assertThat(recipient.getInbox()).hasSize(1);
        assertThat(recipient.getInbox().containsKey(sender.getId())).isTrue();
        assertThat(recipient.getInbox().get(sender.getId())).isEqualTo(sender.getOutbox().get(recipient.getId()));
    }

    @Test
    void userCanOnlySendMessageToOnlyUsersInTheirFriendList() throws UserAuthException, FriendRequestException {
        User sender = userServiceImpl.registerNative(Ismail);
        User recipient = userServiceImpl.registerNative(Kabiru);

        userServiceImpl.sendFriendRequest(sender.getId(), recipient.getId());
        assertThrows(UnSupportedActionException.class, () -> userServiceImpl.sendChatMessage(sender.getId(), recipient.getId(), "How far"));
    }

    @Test
    void userCanReplyToASpecificSentMessage() throws UserAuthException, FriendRequestException, UserNotFoundException, UnSupportedActionException {
        User sender = userServiceImpl.registerNative(Ismail);
        User recipient = userServiceImpl.registerNative(Kabiru);

        userServiceImpl.sendFriendRequest(sender.getId(), recipient.getId());
        recipient.requestHandler(recipient.getFriendRequests().get(0), ACCEPTED);
       String senderMessageId =  userServiceImpl.sendChatMessage(sender.getId(), recipient.getId(), "Hello buddy");
       String recipientMessageId =  userServiceImpl.sendChatMessage(recipient.getId(), sender.getId(), "Hi dear, i trust you're ok", senderMessageId);

        System.out.println(sender.getInbox());
        System.out.println(recipient.getOutbox());

       assertThat(recipient.getInbox().containsKey(sender.getId())).isTrue();

       assertThat(recipient.getInbox()).hasSize(1);
       assertThat(recipient.getOutbox()).hasSize(1);

        assertThat(sender.getInbox()).hasSize(1);
        assertThat(sender.getOutbox()).hasSize(1);

       assertThat(recipient.getInbox().get(sender.getId()).get(0)).isEqualTo(sender.getOutbox().get(recipient.getId()).get(0));
       assertThat(sender.getOutbox().get(recipient.getId()).get(0)).isEqualTo(recipient.getInbox().get(sender.getId()).get(0));


        recipient.getOutbox().get(sender.getId()).forEach(message -> {
            assertThat(message.containsKey(recipientMessageId)).isTrue();
            assertThat(message.get(recipientMessageId).getLinkedMessages()).contains(senderMessageId);
        });

        sender.getInbox().get(recipient.getId()).forEach(message -> {
            assertThat(message.containsKey(recipientMessageId)).isTrue();
            assertThat(message.get(recipientMessageId).getLinkedMessages()).contains(senderMessageId);
        });
    }

    @Test
    void canCreateChatRoomWithOneAdminAndOneMember() throws UserAuthException {

        User admin = userServiceImpl.registerNative(Ismail);
        User whale = userServiceImpl.registerNative(Kabiru);

        ChatRoom chatRoom = new ChatRoom(admin.getId(), "pentax", whale.getId());
        assertThat(chatRoom.getAdmins()).hasSize(1);
        assertThat(chatRoom.getGroupId()).isNotNull();
        assertThat(chatRoom.getGroupName()).isEqualTo("pentax");
        assertThat(chatRoom.getMembers()).hasSize(1);
    }

    @Test
    void canCreateChatRoomWithOneAdminAndTwoMember() throws UserAuthException {
        User admin = userServiceImpl.registerNative(Ismail);
        User whale = userServiceImpl.registerNative(Kabiru);
        User mujibat = userServiceImpl.registerNative(Mujibat);

        ChatRoom chatRoom = new ChatRoom(admin.getId(), "pentax", whale.getId(), mujibat.getId());
        assertThat(chatRoom.getAdmins()).hasSize(1);
        assertThat(chatRoom.getGroupId()).isNotNull();
        assertThat(chatRoom.getGroupName()).isEqualTo("pentax");
        assertThat(chatRoom.getMembers()).hasSize(2);
    }

    @Test
    void canSendChatRoomRequestToMembers() throws UserAuthException, FriendRequestException {
        User admin = userServiceImpl.registerNative(Ismail);
        User whale = userServiceImpl.registerNative(Kabiru);
        User mujibat = userServiceImpl.registerNative(Mujibat);

        userServiceImpl.sendFriendRequest(whale.getId(), mujibat.getId());
    }
}