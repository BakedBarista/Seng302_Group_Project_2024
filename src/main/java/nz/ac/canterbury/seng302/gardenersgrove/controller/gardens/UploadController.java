package nz.ac.canterbury.seng302.gardenersgrove.controller.gardens;


import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * Controller for handling file uploads
 */
@Controller
public class UploadController {
    Logger logger = LoggerFactory.getLogger(UploadController.class);
    private final PlantService plantService;

    private final GardenUserService gardenUserService;

    private final GardenService gardenService;

    private static final List<String> allowedExtension = Arrays.asList("jpg", "png", "svg");


    public UploadController(PlantService plantService, GardenUserService gardenUserService, GardenService gardenService) {
        this.plantService = plantService;
        this.gardenUserService = gardenUserService;
        this.gardenService = gardenService;

    }
    /**
     * Displays the file upload page
     */
    @GetMapping("/uploadImage")
    public String displayUploadForm(@RequestParam(name = "garden_Id") Long gardenId,
                                    @RequestParam(name = "plant_Id") Long plantId,
                                    Model model) {
        model.addAttribute("plantId", plantId);
        model.addAttribute("gardenId", gardenId);
        GardenUser owner = gardenUserService.getCurrentUser();
        List<Garden> gardens = gardenService.getGardensByOwnerId(owner.getId());
        model.addAttribute("gardens", gardens);
        return "images/uploadPage";
    }

    @PostMapping("/uploadImage")
    public String uploadImage(RedirectAttributes redirectAttributes,
                              @RequestParam("image")MultipartFile file,
                              @RequestParam(name = "garden_Id")Long gardenId,
                              @RequestParam(name = "plant_Id")Long plantId) throws IOException, MultipartException {
        Optional<Plant> optionalPlant = plantService.getPlantById(plantId);
        String filename = file.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        //Check types
        if(!allowedExtension.contains(extension.toLowerCase())) {
            redirectAttributes.addAttribute("plant_Id", plantId);
            redirectAttributes.addAttribute("garden_Id", gardenId);
            redirectAttributes.addFlashAttribute("fileTypeError", "Image must be of type png, jpg or svg");
            return "redirect:/uploadImage";
        }
        Path directory = Paths.get("./images/").normalize();
        String fileName = "plant" + plantId + "image" + "-" + filename;
        Path filePath = directory.resolve(fileName).normalize();
        // Check that we don't write outside of the directory
        if (!filePath.startsWith(directory)) {
            throw new IOException("Invalid file path: " + filePath);
        }
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        //Check size
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            redirectAttributes.addAttribute("plant_Id", plantId);
            redirectAttributes.addAttribute("garden_Id", gardenId);
            redirectAttributes.addFlashAttribute("fileSizeError", "Image must be less than 10MB");
            Files.delete(filePath);
            return "redirect:/uploadImage";
        }
        if(optionalPlant.isPresent()){
            Plant plant = optionalPlant.get();
            plant.setPlantImagePath("/" + fileName);
            plantService.setPlantImage(plant);
        }else{
            throw new RuntimeException("Not Found");
        }
        redirectAttributes.addAttribute("plant_Id", plantId);
        redirectAttributes.addAttribute("garden_Id", gardenId);
        return "redirect:/gardens/" + gardenId + "/plants/" + plantId+ "/edit";
    }

    public String upload(MultipartFile file, Long plantId) throws Exception {
        Optional<Plant> optionalPlant = plantService.getPlantById(plantId);
        String filename = file.getOriginalFilename();
        Path directory = Paths.get("./images/").normalize();
        String fileName = "plant" + plantId + "image" + "-" + filename;
        Path filePath = directory.resolve(fileName).normalize();
        // Check that we don't write outside of the directory
        if (!filePath.startsWith(directory)) {
            throw new IOException("Invalid file path: " + filePath);
        }
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        if(optionalPlant.isPresent()){
            Plant plant = optionalPlant.get();
            plant.setPlantImagePath("/" + fileName);
            plantService.setPlantImage(plant);
        }else{
            throw new RuntimeException("Not Found");
        }
        return "Uploaded" + file.getOriginalFilename();
    }


}