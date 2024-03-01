package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controller for handling file uploads
 */
@Controller
public class UploadController {
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";
    private final PlantService plantService;

    public UploadController(PlantService plantService) {this.plantService = plantService;}
    /**
     * Displays the file upload page
     */
    @GetMapping("/gardens/{id}/plants/uploadImage")
    public String displayUploadForm(@RequestParam("id")String id,
                                    Model model) {
        model.addAttribute("id", id);
        return "images/uploadPage";
    }

    @PostMapping("/gardens/{id}/plants/uploadImage")
    public String uploadImage(Model model,
                              @RequestParam("image")MultipartFile file,
                              @RequestParam("id") Long id) throws IOException {
        StringBuilder fileName = new StringBuilder();
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
        fileName.append(file.getOriginalFilename());
        Files.write(fileNameAndPath, file.getBytes());
        plantService.setPlantImage(id,fileNameAndPath.toString());
        model.addAttribute("msg", "Uploaded images: " + fileName.toString());
        return "imageupload/index";
    }
}
