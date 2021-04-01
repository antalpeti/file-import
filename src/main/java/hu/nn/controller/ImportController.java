package hu.nn.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.tika.detect.AutoDetectReader;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Splitter;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import hu.nn.constant.CSVConstant;
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

            Charset detectedCharset = detectCharset(path);
            String detectedSeparator = detectSeparator(path, detectedCharset);
            processFileWithSplitter(Files.newInputStream(path), detectedCharset, detectedSeparator);
            processFileWithCSVReader(path, detectedCharset, detectedSeparator.charAt(0));
            log.info("Successfull import. originalFileName: {}", originalFileName);
            attributes.addFlashAttribute("success", "Import successfull: " + originalFileName);
        } catch (IOException e) {
            log.error("Error during import: {}", e);
            attributes.addFlashAttribute("danger", "Import failed: " + e);
        }

        return "redirect:/";
    }

    private static Charset detectCharset(final Path path) throws IOException {
        log.info("detectSeparator called.");
        final Charset charset;

        try (final InputStream input = new BufferedInputStream(Files.newInputStream(path)); final AutoDetectReader detector = new AutoDetectReader(input)) {
            charset = detector.getCharset();
        } catch (TikaException e) {
            throw new IOException("Unable to detect charset: {}", e);
        }

        return charset;
    }

    private String detectSeparator(Path path, Charset charset) {
        log.info("detectSeparator called.");
        try {
            String content = Files.readString(path, charset);
            if (content.contains(CSVConstant.SEPARATOR_SEMICOLON)) {
                return CSVConstant.SEPARATOR_SEMICOLON;
            } else if (content.contains(CSVConstant.SEPARATOR_PIPE)) {
                return CSVConstant.SEPARATOR_PIPE;
            }
        } catch (IOException e) {
            log.error("Error during detecting the separator: {}", e);
        }
        return "";
    }

    private void processFileWithSplitter(InputStream inputStream, Charset charset, String separator) {
        log.info("processFileWithSplitter called.");
        Scanner sc = new Scanner(inputStream, charset);
        while (sc.hasNext()) {
            String nextLine = sc.nextLine();
            log.info("nextLine: {}", nextLine);
            List<String> splitToList = Splitter.on(separator).splitToList(nextLine);
            log.info("splitToList: {}", splitToList);
        }
    }

    private void processFileWithCSVReader(Path path, Charset charset, char separator) {
        log.info("processFileWithCSVReader called.");

        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
            BufferedReader br = Files.newBufferedReader(path, charset);
            CSVReader csvReader = new CSVReaderBuilder(br).withCSVParser(parser).build();
            String[] csvrow = null;
            while ((csvrow = csvReader.readNext()) != null) {
                log.info(Arrays.toString(csvrow));
            }
        } catch (Exception e) {
            log.error("Error during csv parsing: {}", e);
        }
    }

}
