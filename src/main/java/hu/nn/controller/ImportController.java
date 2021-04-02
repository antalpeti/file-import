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
import java.util.ArrayList;
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

    private static final String IMPORT_DIR = "./uploads/";
    private static final String REDIRECT = "redirect:/";

    @GetMapping("/")
    public String homepage() {
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        log.info("uploadFile called.");
        String userDir = System.getProperty("user.dir");
        log.info("user.dir:{}", userDir);
        Path path = Paths.get(userDir + IMPORT_DIR.substring(1));
        if (!Files.exists(path)) {
            String pathAsString = path.toString();
            log.warn("Missing directory. pathAsString: {}", pathAsString);
            attributes.addFlashAttribute("warning", "Missing directory: " + pathAsString);
            return REDIRECT;
        } else if (file.isEmpty()) {
            String originalFileName = file.getOriginalFilename();
            log.warn("Empty file. originalFileName: {}", originalFileName);
            attributes.addFlashAttribute("warning", "Empty file: " + originalFileName);
            return REDIRECT;
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            path = Paths.get(IMPORT_DIR + originalFileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            Charset detectedCharset = detectCharset(path);
            String detectedSeparator = detectSeparator(path, detectedCharset);
            processFile(path, detectedCharset, detectedSeparator);
            log.info("Successfull import. originalFileName: {}", originalFileName);
            attributes.addFlashAttribute("success", "Import successfull: " + originalFileName);
        } catch (IOException e) {
            log.error("Error in uploadFile: {}", e);
            attributes.addFlashAttribute("danger", "Import failed: " + e);
        }

        return REDIRECT;
    }

    private static Charset detectCharset(final Path path) throws IOException {
        log.info("detectCharset called. path: {}", path);
        final Charset charset;

        try (final InputStream input = new BufferedInputStream(Files.newInputStream(path)); final AutoDetectReader detector = new AutoDetectReader(input)) {
            charset = detector.getCharset();
        } catch (TikaException e) {
            throw new IOException("Error in detectCharset: {}", e);
        }

        log.info("Detected charset. charset: {}", charset);
        return charset;
    }

    private String detectSeparator(Path path, Charset charset) {
        log.info("detectSeparator called. path: {}, charset: {}", path, charset);
        String separator = "";
        try {
            String content = Files.readString(path, charset);
            if (content.contains(CSVConstant.SEPARATOR_SEMICOLON)) {
                separator = CSVConstant.SEPARATOR_SEMICOLON;
            } else if (content.contains(CSVConstant.SEPARATOR_PIPE)) {
                separator = CSVConstant.SEPARATOR_PIPE;
            }
        } catch (IOException e) {
            log.error("Error in detectSeparator: {}", e);
        }
        log.info("Detected separator. separator:{}", separator);
        return separator;
    }

    private void processFileWithSplitter(Path path, Charset charset, String separator) {
        log.info("processFileWithSplitter called. path: {}, charset: {}, separator: {}", path, charset, separator);
        try (Scanner sc = new Scanner(Files.newInputStream(path), charset)) {
            while (sc.hasNext()) {
                String line = sc.nextLine();
                log.info("nextLine: {}", line);
                List<String> splitedLine = Splitter.on(separator).splitToList(line);
                log.info("splitedLine: {}", splitedLine);
            }
        } catch (IOException e) {
            log.error("Error in processFileWithSplitter: {}", e);
        }
    }

    private List<String> processFileWithCSVReader(Path path, Charset charset, char separator) {
        log.info("processFileWithCSVReader called. path: {}, charset: {}, separator: {} ", path, charset, separator);
        List<String> processedRows = new ArrayList<>();
        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
            BufferedReader br = Files.newBufferedReader(path, charset);
            CSVReader csvReader = new CSVReaderBuilder(br).withCSVParser(parser).build();
            String[] csvrow = null;
            while ((csvrow = csvReader.readNext()) != null) {
                String processedRow = Arrays.toString(csvrow);
                processedRows.add(processedRow);
                log.info("Processed line: {}", processedRow);
            }
        } catch (Exception e) {
            log.error("Error in processFileWithCSVReader: {}", e);
        }
        return processedRows;
    }

    private void processFile(Path path, Charset charset, String separator) {
        log.info("processFile called. path: {}, charset: {}, separator: {}", path, charset, separator);
        switch (separator) {
        case CSVConstant.SEPARATOR_SEMICOLON:
            processFileForOutPayHeader(path, charset);
            break;
        default:
            break;
        }
    }

    private void processFileForOutPayHeader(Path path, Charset charset) {
        log.info("processFileForOutPayHeader called. path: {}, charset: {}", path, charset);
        List<String> processedList = processFileWithCSVReader(path, charset, CSVConstant.SEPARATOR_SEMICOLON.charAt(0));
    }
}
