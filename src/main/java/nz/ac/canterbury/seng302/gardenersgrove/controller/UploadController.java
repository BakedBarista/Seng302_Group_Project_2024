package nz.ac.canterbury.seng302.gardenersgrove.controller;


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
import java.util.Arrays;
import java.util.List;

/**
 * Controller class for uploading images
 */
@Controller
public class UploadController {
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("png", "jpg", "jpeg", "svg");
    @GetMapping("/uploadimage")
    public String displayUploadForm() {
        return "images/uploadPage";
    }

    @PostMapping("/upload")
    public String uploadImage(Model model,
                              @RequestParam("image") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload");
            return "images/uploadPage";
        }
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        if(!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            model.addAttribute("fileTypeError", "Only PNG,JPG, and SVG files are allowed");
            return "images/uploadPage";
        }

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, originalFilename);
        Files.write(fileNameAndPath,file.getBytes());

        model.addAttribute("message","File uploaded successfully");
        model.addAttribute("msg", "Uploaded images: " + originalFilename);
        return "images/uploadPage";
    } //TODO: show error message on page
}
