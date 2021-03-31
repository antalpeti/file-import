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
import org.apache.tika.metadata.Metadata;
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

            Charset detectedCharset = detectCharset(path, new Metadata());
            processFileWithSplitter(Files.newInputStream(path), detectedCharset);
            processFileWithCSVReader(path, detectedCharset);
            log.info("Successfull import. originalFileName: {}", originalFileName);
            attributes.addFlashAttribute("success", "Import successfull: " + originalFileName);
        } catch (IOException e) {
            log.error("Error during import: {}", e);
            attributes.addFlashAttribute("danger", "Import failed: " + e);
        }

        return "redirect:/";
    }

    private static Charset detectCharset(final Path path, final Metadata metadata) throws IOException {
        final Charset charset;

        String orig = metadata.get(Metadata.CONTENT_ENCODING);

        if (null != orig && Charset.isSupported(orig)) {
            return Charset.forName(orig);
        }

        try (final InputStream input = new BufferedInputStream(Files.newInputStream(path));
                final AutoDetectReader detector = new AutoDetectReader(input, metadata)) {
            charset = detector.getCharset();
        } catch (TikaException e) {
            throw new IOException("Unable to detect charset.", e);
        }

        return charset;
    }

    private void processFileWithSplitter(InputStream inputStream, Charset detectedCharset) {
        log.info("processFileWithSplitter called.");
        Scanner sc = new Scanner(inputStream, detectedCharset);
        String separator = "|";
        while (sc.hasNext()) {
            String nextLine = sc.nextLine();
            log.info("nextLine: {}", nextLine);
            List<String> splitToList = Splitter.on(separator).splitToList(nextLine);
            log.info("splitToList: {}", splitToList);
        }
    }

    private void processFileWithCSVReader(Path path, Charset detectedCharset) {
        log.info("processFileWithCSVReader called.");

        try {
            CSVParser parser = new CSVParserBuilder().withSeparator('|').build();
            BufferedReader br = Files.newBufferedReader(path, detectedCharset);
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
