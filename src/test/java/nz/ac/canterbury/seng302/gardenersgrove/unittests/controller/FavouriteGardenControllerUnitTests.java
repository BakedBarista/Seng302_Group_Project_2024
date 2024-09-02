package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.FavouriteGardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FavouriteGardenControllerUnitTests {

    @Mock
    private GardenService gardenService;

    @Mock
    private GardenUserService gardenUserService;

    @InjectMocks
    private FavouriteGardenController favouriteGardenController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFavouriteGarden() {
        GardenUser mockUser = new GardenUser();
        List<Garden> mockGardens = List.of(new Garden("Rose Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"test description", 100.0, null, null));

        when(gardenUserService.getCurrentUser()).thenReturn(mockUser);
        when(gardenService.getPublicGardensByOwnerId(mockUser)).thenReturn(mockGardens);

        ResponseEntity<List<Garden>> response = favouriteGardenController.favouriteGarden("rose");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockGardens, response.getBody());
    }

    @Test
    void testUpdateFavouriteGarden() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(Map.of("id", "1"));
        Model model = mock(Model.class);

        GardenUser mockUser = new GardenUser();
        Garden mockGarden = new Garden();

        when(gardenUserService.getCurrentUser()).thenReturn(mockUser);
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(mockGarden));

        ResponseEntity<String> response = favouriteGardenController.updateFavouriteGarden(json, model);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Favourite Garden Updated", response.getBody());
        verify(gardenService, times(1)).addFavouriteGarden(mockUser.getId(), mockGarden.getId());
    }

    @Test
    void testUpdateFavouriteGardenInvalidJson() throws Exception {
        String invalidJson = "{invalid}";

        Model model = mock(Model.class);

        ResponseEntity<String> response = favouriteGardenController.updateFavouriteGarden(invalidJson, model);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid json format", response.getBody());
    }
}