package hu.nn.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ImportController {

    private final String IMPORT_DIR = "./uploads/";

    @GetMapping("/")
    public String homepage() {
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        log.info("user.dir:{}", System.getProperty("user.dir"));

        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file for import.");
            return "redirect:/";
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            Path path = Paths.get(IMPORT_DIR + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Error during file upload {}", e);
        }

        attributes.addFlashAttribute("message", "You successfully imported " + fileName + '!');

        return "redirect:/";
    }

}
