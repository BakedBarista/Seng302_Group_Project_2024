package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.ManageFriendsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RequestService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ManageFriendsControllerTest {
    @Mock
    private GardenService gardenService;

    @Mock
    private FriendService friendService;

    @Mock
    private RequestService requestService;

    @Mock
    private GardenUserService gardenUserService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ManageFriendsController manageFriendsController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
//        GardenUser mockUser = mock(GardenUser.class);
//        when(mockUser.getId()).thenReturn(1L);
//        when(gardenUserService.getCurrentUser()).thenReturn(mockUser);
    }

    @Test
    public void testManageFriends() {
        Model model = mock(Model.class);
        String result = manageFriendsController.manageFriends(authentication, model);

        verify(model).addAttribute(eq("friends"), anyList());
        verify(model).addAttribute(eq("allUsers"), anyList());
        verify(model).addAttribute(eq("sentRequests"), anyList());
        verify(model).addAttribute(eq("receivedRequests"), anyList());

        assertEquals("users/manageFriends", result);
    }
}
