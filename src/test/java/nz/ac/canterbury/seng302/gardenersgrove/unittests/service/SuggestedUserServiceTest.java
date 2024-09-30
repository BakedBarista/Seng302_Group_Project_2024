package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.SuggestedUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.BirthFlowerService;
import nz.ac.canterbury.seng302.gardenersgrove.service.CompatibilityService;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SuggestedUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.thymeleaf.TemplateEngine;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SuggestedUserServiceTest {

    private SuggestedUserService suggestedUserService;
    private FriendService friendService;
    private CompatibilityService compatibilityService;
    private BirthFlowerService birthFlowerService;
    private TemplateEngine templateEngine;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;

    Long loggedInUserId = 1L;
    GardenUser loggedInUser;
    Long suggestedUserId = 2L;
    GardenUser suggestedUser;

    @BeforeEach
    public void setUp() {
        friendService = Mockito.mock(FriendService.class);
        compatibilityService = Mockito.mock(CompatibilityService.class);
        birthFlowerService = Mockito.mock(BirthFlowerService.class);
        templateEngine = Mockito.mock(TemplateEngine.class);
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        servletContext = Mockito.mock(ServletContext.class);
        suggestedUserService = new SuggestedUserService(friendService, compatibilityService, birthFlowerService, templateEngine);

        loggedInUser = new GardenUser();
        suggestedUser = new GardenUser();

        loggedInUser.setId(loggedInUserId);
        suggestedUser.setId(suggestedUserId);
    }

    @Test
    void whenPendingRecordExists_thenReturnTrue() {
        Friends friendRecord = new Friends(loggedInUser, suggestedUser, PENDING);
        Mockito.when(friendService.getSentRequests(loggedInUserId)).thenReturn(List.of(friendRecord));

        boolean result = suggestedUserService.friendRecordExists(loggedInUserId, suggestedUserId);

        Assertions.assertTrue(result);
    }

    @Test
    void whenDeclinedRecordExists_thenReturnTrue() {
        Friends friendRecord = new Friends(loggedInUser, suggestedUser, DECLINED);
        Mockito.when(friendService.getSentRequests(loggedInUserId)).thenReturn(List.of(friendRecord));

        boolean result = suggestedUserService.friendRecordExists(loggedInUserId, suggestedUserId);

        Assertions.assertTrue(result);
    }

    @Test
    void whenNoRecordExists_thenReturnFalse() {
        Mockito.when(friendService.getSentRequests(loggedInUserId)).thenReturn(Collections.emptyList());

        boolean result = suggestedUserService.friendRecordExists(loggedInUserId, suggestedUserId);

        Assertions.assertFalse(result);
    }

    @Test
    void whenAcceptedRecordExists_thenReturnFalse() {
        Friends friendRecord = new Friends(loggedInUser, suggestedUser, ACCEPTED);
        Mockito.when(friendService.getSentRequests(loggedInUserId)).thenReturn(List.of(friendRecord));

        boolean result = suggestedUserService.friendRecordExists(loggedInUserId, suggestedUserId);

        Assertions.assertFalse(result);
    }

    @Test
    void testAttemptToAcceptPendingRequest_WhenRequestExists_ThenAcceptRequest() {
        Friends pendingFriendship = new Friends();
        pendingFriendship.setStatus(PENDING);

        Mockito.when(friendService.getPendingFriendRequest(loggedInUserId, suggestedUserId)).thenReturn(Optional.of(pendingFriendship));

        boolean result = suggestedUserService.attemptToAcceptPendingRequest(loggedInUserId, suggestedUserId);

        Assertions.assertEquals(ACCEPTED, pendingFriendship.getStatus());
        Mockito.verify(friendService, times(1)).save(pendingFriendship);
        Assertions.assertTrue(result);
    }

    @Test
    void testAttemptToAcceptPendingRequest_WhenNoRequestExists_ThenDoNothing() {
        Mockito.when(friendService.getPendingFriendRequest(suggestedUserId, loggedInUserId)).thenReturn(Optional.empty());

        boolean result = suggestedUserService.attemptToAcceptPendingRequest(loggedInUserId, suggestedUserId);

        Mockito.verify(friendService, never()).save(any(Friends.class));
        Assertions.assertFalse(result);
    }

    @Test
    void testAttemptToDeclinePendingRequest_WhenRequestExists_ThenDeclineRequest() {
        Friends pendingFriendship = new Friends();
        pendingFriendship.setStatus(PENDING);

        Mockito.when(friendService.getPendingFriendRequest(loggedInUserId, suggestedUserId)).thenReturn(Optional.of(pendingFriendship));

        boolean result = suggestedUserService.attemptToDeclinePendingRequest(loggedInUserId, suggestedUserId);

        Assertions.assertEquals(DECLINED, pendingFriendship.getStatus());
        Mockito.verify(friendService, times(1)).save(pendingFriendship);
        Assertions.assertTrue(result);
    }

    @Test
    void testAttemptToDeclinePendingRequest_WhenNoRequestExists_ThenDoNothing() {
        Mockito.when(friendService.getPendingFriendRequest(suggestedUserId, loggedInUserId)).thenReturn(Optional.empty());

        boolean result = suggestedUserService.attemptToDeclinePendingRequest(loggedInUserId, suggestedUserId);

        Mockito.verify(friendService, never()).save(any(Friends.class));
        Assertions.assertFalse(result);
    }

    @Test
    void testSendNewPendingRequest_FriendRecordExists_NothingSentReturnFalse() {
        SuggestedUserService suggestedUserServiceSpy = Mockito.spy(suggestedUserService);
        doReturn(true).when(suggestedUserServiceSpy).friendRecordExists(loggedInUser.getId(), suggestedUser.getId());

        boolean result = suggestedUserServiceSpy.sendNewPendingRequest(loggedInUser, suggestedUser);

        Mockito.verify(friendService, never()).save(any(Friends.class));
        Assertions.assertFalse(result);
    }

    @Test
    void testSendNewRequest_NoFriendRecordExists_SendPendingPendingRequestReturnTrue() {
        SuggestedUserService suggestedUserServiceSpy = Mockito.spy(suggestedUserService);
        doReturn(false).when(suggestedUserServiceSpy).friendRecordExists(loggedInUser.getId(), suggestedUser.getId());

        boolean result = suggestedUserServiceSpy.sendNewPendingRequest(loggedInUser, suggestedUser);

        Mockito.verify(friendService, times(1)).save(any(Friends.class));
        Assertions.assertTrue(result);
    }

    @Test
    void testSetDeclinedFriendship_PendingRecordExists_NothingSentReturnFalse() {
        SuggestedUserService suggestedUserServiceSpy = Mockito.spy(suggestedUserService);
        doReturn(true).when(suggestedUserServiceSpy).friendRecordExists(loggedInUser.getId(), suggestedUser.getId());

        boolean result = suggestedUserServiceSpy.setDeclinedFriendship(loggedInUser, suggestedUser);

        Mockito.verify(friendService, never()).save(any(Friends.class));
        Assertions.assertFalse(result);
    }

    @Test
    void testSetDeclinedFriendship_AcceptedRecordExists_NothingSentReturnFalse() {
        Mockito.when(friendService.getAcceptedFriendship(loggedInUserId, suggestedUserId)).thenReturn(new Friends(loggedInUser, suggestedUser, ACCEPTED));

        boolean result = suggestedUserService.setDeclinedFriendship(loggedInUser, suggestedUser);

        Mockito.verify(friendService, never()).save(any(Friends.class));
        Assertions.assertFalse(result);
    }

    @Test
    void testSetDeclinedFriendship_NoFriendRecordExists_SetDeclinedFriendshipReturnFalse() {
        SuggestedUserService suggestedUserServiceSpy = Mockito.spy(suggestedUserService);
        doReturn(false).when(suggestedUserServiceSpy).friendRecordExists(loggedInUser.getId(), suggestedUser.getId());
        Mockito.when(friendService.getAcceptedFriendship(loggedInUserId, suggestedUserId)).thenReturn(null);

        boolean result = suggestedUserServiceSpy.sendNewPendingRequest(loggedInUser, suggestedUser);

        Mockito.verify(friendService, times(1)).save(any(Friends.class));
        Assertions.assertTrue(result);
    }

    @Test
    void testValidationCheck_WithOwnId_ReturnFalse() {
        boolean result = suggestedUserService.validationCheck(loggedInUserId, loggedInUserId);

        Assertions.assertFalse(result);
    }

    @Test
    void testValidationCheck_AlreadyFriends_ReturnFalse() {
        when(friendService.getAcceptedFriendship(loggedInUserId, suggestedUserId)).thenReturn(new Friends());

        boolean result = suggestedUserService.validationCheck(loggedInUserId, suggestedUserId);

        Assertions.assertFalse(result);
    }

    @Test
    void testValidationCheck_AlreadyDeclined_ReturnFalse() {
        when(friendService.getDeclinedFriendship(loggedInUserId, suggestedUserId)).thenReturn(Optional.of(new Friends()));

        boolean result = suggestedUserService.validationCheck(loggedInUserId, suggestedUserId);

        Assertions.assertFalse(result);
    }

    @Test
    void testValidationCheck_AllChecksPass_ReturnTrue() {
        when(friendService.getAcceptedFriendship(loggedInUserId, suggestedUserId)).thenReturn(null);
        when(friendService.getDeclinedFriendship(loggedInUserId, suggestedUserId)).thenReturn(Optional.empty());

        boolean result = suggestedUserService.validationCheck(loggedInUserId, suggestedUserId);

        Assertions.assertTrue(result);
    }

    @Test
    void whenGetSortedSuggestedUserDTOs_thenSortedCorrectly() {
        GardenUser user = new GardenUser();
        GardenUser user1 = new GardenUser();
        GardenUser user2 = new GardenUser();
        GardenUser user3 = new GardenUser();
        GardenUser user4 = new GardenUser();
        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);
        user4.setId(4L);

        Mockito.when(compatibilityService.friendshipCompatibilityQuotient(user, user1)).thenReturn(70.0);
        Mockito.when(compatibilityService.friendshipCompatibilityQuotient(user, user2)).thenReturn(90.0);
        Mockito.when(compatibilityService.friendshipCompatibilityQuotient(user, user3)).thenReturn(60.0);
        Mockito.when(compatibilityService.friendshipCompatibilityQuotient(user, user4)).thenReturn(80.0);

        Mockito.when(friendService.receivedConnectionRequests(user)).thenReturn(List.of(user1, user2));
        Mockito.when(friendService.availableConnections(user)).thenReturn(List.of(user3, user4));

        Mockito.when(request.getServletContext()).thenReturn(servletContext);

        List<SuggestedUserDTO> result = suggestedUserService.getSuggestionFeedContents(user, request, response);

        // checking the right order
        assertEquals(4, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(1L, result.get(1).getId());
        assertEquals(4L, result.get(2).getId());
        assertEquals(3L, result.get(3).getId());
    }
}
