package hu.nn.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Splitter;
import com.opencsv.CSVReader;

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

            processFileWithSplitter(Files.newInputStream(path));
            processFileWithCSVReader(Files.newInputStream(path));
            log.info("Successfull import. originalFileName: {}", originalFileName);
            attributes.addFlashAttribute("success", "Import successfull: " + originalFileName);
        } catch (IOException e) {
            log.error("Error during import: {}", e);
            attributes.addFlashAttribute("danger", "Import failed: " + e);
        }

        return "redirect:/";
    }

    private void processFileWithSplitter(InputStream inputStream) {
        log.info("processFileWithSplitter called.");
        Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8.name());
        String separator = "|";
        while (sc.hasNext()) {
            String nextLine = sc.nextLine();
            log.info("nextLine: {}", nextLine);
            List<String> splitToList = Splitter.on(separator).splitToList(nextLine);
            log.info("splitToList: {}", splitToList);
        }
    }

    private void processFileWithCSVReader(InputStream inputStream) {
        log.info("processFileWithCSVReader called.");
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8.name()))) {
            List<String[]> lines = csvReader.readAll();
            for (String[] line : lines) {
                log.info("CSV line: {}", Arrays.toString(line));
            }
        } catch (Exception e) {
            log.error("Error during csv parsing: {}", e);
        }
    }

}
