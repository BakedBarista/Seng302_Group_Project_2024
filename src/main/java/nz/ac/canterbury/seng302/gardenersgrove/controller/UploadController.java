package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.h2.table.Plan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    private static final List<String> allowedExtension = Arrays.asList("jpg", "png", "svg");


    public UploadController(PlantService plantService) {this.plantService = plantService;}
    /**
     * Displays the file upload page
     */
    @GetMapping("/uploadImage")
    public String displayUploadForm(@RequestParam(name = "gardenId") Long gardenId,
                                    @RequestParam(name = "plantId") Long plantId,
                                    Model model) {
        model.addAttribute("plantId", plantId);
        model.addAttribute("gardenId", gardenId);
        return "images/uploadPage";
    }

    @PostMapping("/uploadImage")
    public String uploadImage(RedirectAttributes redirectAttributes,
                              @RequestParam("image")MultipartFile file,
                              @RequestParam(name = "gardenId")Long gardenId,
                              @RequestParam(name = "plantId")Long plantId) throws IOException, MultipartException {
        logger.info("/uploadImage", file, gardenId,plantId);
        Optional<Plant> optionalPlant = plantService.getPlantById(plantId);
        String filename = file.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        //Check types
        if(!allowedExtension.contains(extension.toLowerCase())) {
            redirectAttributes.addAttribute("plantId", plantId);
            redirectAttributes.addAttribute("gardenId", gardenId);
            redirectAttributes.addFlashAttribute("error", "Only JPG, PNG and SVG are allowed");
            return "redirect:/uploadImage";
        }
        //Check size
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            redirectAttributes.addAttribute("plantId", plantId);
            redirectAttributes.addAttribute("gardenId", gardenId);
            redirectAttributes.addFlashAttribute("fileSizeError", "Exceeded max file size of 10MB");
            //Files.delete(filePath);
            return "redirect:/uploadImage";
        }
        //String directory = "./public/";
        String fileName = "plant" + plantId + "image" + "-" + filename;
        Path filePath = Paths.get(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        if(optionalPlant.isPresent()){
            Plant plant = optionalPlant.get();
            plant.setPlantImagePath(fileName);
            plantService.setPlantImage(plant);
        }else{
            throw new RuntimeException("Not Found");
        }
        redirectAttributes.addAttribute("plantId", plantId);
        redirectAttributes.addAttribute("gardenId", gardenId);
        return "redirect:/gardens/" + gardenId + "/plants/" + plantId+ "/edit";
    }

}
