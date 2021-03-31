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
        String userDir = System.getProperty("user.dir");
        log.info("user.dir:{}", userDir);
        Path path = Paths.get(userDir + IMPORT_DIR.substring(1));
        if (!Files.exists(path)) {
            String pathAsString = path.toString();
            log.warn("Missing directory. pathAsString: {}", pathAsString);
            attributes.addFlashAttribute("warning", "Missing directory: " + pathAsString);
            return "redirect:/";
        } else if (file.isEmpty()) {
            String originalFileName = file.getOriginalFilename();
            log.warn("Empty file. originalFileName: {}", originalFileName);
            attributes.addFlashAttribute("warning", "Empty file: " + originalFileName);
            return "redirect:/";
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            path = Paths.get(IMPORT_DIR + originalFileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            log.info("Successfull import. originalFileName: {}", originalFileName);
            attributes.addFlashAttribute("success", "Import successfull: " + originalFileName);
        } catch (IOException e) {
            log.error("Error during import: {}", e);
            attributes.addFlashAttribute("danger", "Import failed: " + e);
        }

        return "redirect:/";
    }

}
