package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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
    @GetMapping("/gardens/{gardenId}/plants/{plantId}/uploadImage")
    public String displayUploadForm(@PathVariable("gardenId")Long gardenId,
                                    @PathVariable("plantId")Long plantId,
                                    Model model) {
        model.addAttribute("gardenId", gardenId);
        model.addAttribute("plantId",plantId);
        return "images/uploadPage";
    }

    @PostMapping("/gardens/{gardenId}/plants/{plantId}/uploadImage")
    public String uploadImage(Model model,
                              @RequestParam("image")MultipartFile file,
                              @PathVariable("gardenId")Long gardenId,
                              @PathVariable("plantId")Long plantId) throws IOException {
        //Check if file is empty, used to display when first open the upload page
        if(file.isEmpty()) {
            model.addAttribute("error", "Please select a file to upload");
            return "images/uploadPage";
        }
        //Check types
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/svg"))) {
            model.addAttribute("fileTypeError", "Only JPG, PNG, and SVG files are allowed");
            return "images/uploadPage";
        }
        //Check size
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            model.addAttribute("fileSizeError", "File size exceeds the limit of 10MB");
            return "images/uploadPage";
        }
        //Generates a unique filename for the saved image to avoid future conflict
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
            Files.write(fileNameAndPath, file.getBytes());
            plantService.setPlantImage(plantId,fileNameAndPath.toString());
            model.addAttribute("msg","Uploaded image: " + fileName);
        } catch (IOException e) {
            model.addAttribute("UploadError", "Failed to upload image");
        }
        return "imageupload/index";
    }
}
