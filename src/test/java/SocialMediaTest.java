import data.dto.NativeDto;
import data.model.RequestStatus;
import data.model.User;
import data.repository.UserDatabaseImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserServiceImpl;
import web.exception.FriendRequestException;
import web.exception.UserAuthException;
import web.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static data.model.RequestStatus.ACCEPTED;
import static org.assertj.core.api.Assertions.assertThat;
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
        User user = userServiceImpl.registerNative(Ismail);
        assertTrue(userServiceImpl.getUserDatabase().contain(user));
        assertEquals(userServiceImpl.getUserDatabase().size(), 1);
    }

    @Test
    void UserIsAutomaticallyLoggedInWhenCreatedTest(){
        User user = userServiceImpl.registerNative(Ismail);
        assertThat(user.isLoggedIn()).isEqualTo(true);
    }

    @Test
    void UserCanLogoutTest() throws UserAuthException {
        User user = userServiceImpl.registerNative(Ismail);
        user.logout();
        assertThat(user.isLoggedIn()).isEqualTo(false);
    }

    @Test
    void UserCanLogInTest() throws UserAuthException {
        User user = userServiceImpl.registerNative(Kabiru);
        user.logout();

        user.login(Kabiru.getEmail(), Kabiru.getPassword());
        assertThat(user.isLoggedIn()).isEqualTo(true);
    }


    @Test
    void can_addMoreThanOne_native(){
        User user1 = userServiceImpl.registerNative(Ismail);
        User user2 = userServiceImpl.registerNative(Kabiru);

        assertTrue(userServiceImpl.getUserDatabase().contain(user1));
        assertTrue(userServiceImpl.getUserDatabase().contain(user2));

        assertEquals(userServiceImpl.getUserDatabase().size(), 2);
    }

    @Test
    void native_canFindOtherUserOnPlatform() throws UserNotFoundException {
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
    void applicationThrowException_whenUserWithSearchNameNotFound() {
        registerUser();
        assertThrows(UserNotFoundException.class, ()-> userServiceImpl.getUsersByName("peter"));
    }

    private void registerUser() {
        User user1 = userServiceImpl.registerNative(Ismail);
        User user2 = userServiceImpl.registerNative(Kabiru);
        User user3 = userServiceImpl.registerNative(Mujibat);
    }

    @Test
    void nativeCan_sendFriendRequests_toAnotherNative() throws FriendRequestException {
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
    void user_canAcceptFriendRequest_andAddFriendFriendToFriendList() throws FriendRequestException {
        User sender = userServiceImpl.registerNative(Ismail);
        User recipient = userServiceImpl.registerNative(Kabiru);

        userServiceImpl.sendFriendRequest(sender.getId(), recipient.getId());
        recipient.requestHandler(recipient.getFriendRequests().get(0), ACCEPTED);

        recipient.getFriends().forEach(System.out::println);

        assertThat(recipient.getId()).isIn(sender.getFriends());
        assertThat(sender.getId()).isIn(recipient.getFriends());
    }

    @Test
    void user_canRejectFriendRequest() throws FriendRequestException {
        User sender = userServiceImpl.registerNative(Ismail);
        User recipient = userServiceImpl.registerNative(Kabiru);

        userServiceImpl.sendFriendRequest(sender.getId(), recipient.getId());
        recipient.requestHandler(recipient.getFriendRequests().get(0), RequestStatus.REJECTED);

        assertThat(recipient.getFriends()).doesNotContain(sender.getId());
        assertThat(recipient.getFriends()).isEmpty();
    }
}