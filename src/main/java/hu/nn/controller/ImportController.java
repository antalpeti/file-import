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

import javax.ws.rs.core.MediaType;

import org.apache.tika.detect.AutoDetectReader;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
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
import hu.nn.dto.OutPayHeaderDTO;
import hu.nn.dto.PolicyDTO;
import hu.nn.mapper.OutPayHeaderMapper;
import hu.nn.mapper.PolicyMapper;
import hu.nn.service.OutPayHeaderService;
import hu.nn.service.PolicyService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ImportController {

    @Autowired
    private OutPayHeaderService outPayHeaderService;

    @Autowired
    private PolicyService policyService;

    private static final String IMPORT_DIR = "./uploads/";
    private static final String REDIRECT = "redirect:/";
    private static final String SUCCESS = "success";
    private static final String WARNING = "warning";
    private static final String DANGER = "danger";
    private static final String IMPORT_FAILED = "Import failed: ";

    private RedirectAttributes redirectAttributes;

    private String cleanedOriginalFileName;

    @GetMapping("/")
    public String homepage() {
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        log.info("uploadFile called.");
        redirectAttributes = attributes;
        String userDir = System.getProperty("user.dir");
        log.info("user.dir:{}", userDir);
        Path path = Paths.get(userDir + IMPORT_DIR.substring(1));
        String originalFileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        boolean fileProcessEnabled = true;
        fileProcessEnabled = isValidFile(file, path, originalFileName, contentType, fileProcessEnabled);

        if (fileProcessEnabled && originalFileName != null) {
            try {
                cleanedOriginalFileName = StringUtils.cleanPath(originalFileName);
                path = Paths.get(IMPORT_DIR + cleanedOriginalFileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                Charset detectedCharset = detectCharset(path);
                String detectedSeparator = detectSeparator(path, detectedCharset);
                processFile(path, detectedCharset, detectedSeparator);
                log.info("Successfull import. originalFileName: {}", cleanedOriginalFileName);
                addSuccessMessage("Import successfull: " + cleanedOriginalFileName);
            } catch (Exception e) {
                log.error("Error in uploadFile during file processing: {}", e);
                addErrorMessage(IMPORT_FAILED + e);
            }
        }

        return REDIRECT;
    }

    private void addSuccessMessage(String message) {
        log.info("addSuccessMessage called. message: {}", message);
        redirectAttributes.addFlashAttribute(SUCCESS, message);
    }

    private void addErrorMessage(String message) {
        log.info("addErrorMessage called. message: {}", message);
        redirectAttributes.addFlashAttribute(DANGER, message);
    }

    private boolean isValidFile(MultipartFile file, Path path, String originalFileName, String contentType, boolean fileProcessEnabled) {
        if (!Files.exists(path)) {
            String pathAsString = path.toString();
            log.warn("Missing directory. pathAsString: {}", pathAsString);
            addWarningMessage("Missing directory: " + pathAsString);
            fileProcessEnabled = false;
        } else if (file.isEmpty()) {
            log.warn("Empty file. originalFileName: {}", originalFileName);
            addWarningMessage("Empty file: " + originalFileName);
            fileProcessEnabled = false;
        } else if (!MediaType.TEXT_PLAIN.equals(contentType)) {
            log.warn("Not plain text file. contentType: {}", contentType);
            fileProcessEnabled = false;
            addWarningMessage("Not plain text file: " + contentType);
        }
        return fileProcessEnabled;
    }

    private void addWarningMessage(String message) {
        log.info("addWarningMessage called. message: {}", message);
        redirectAttributes.addFlashAttribute(WARNING, message);
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
        } catch (Exception e) {
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
        } catch (Exception e) {
            log.error("Error in processFileWithSplitter: {}", e);
        }
    }

    private List<String[]> processFileWithCSVReader(Path path, Charset charset, char separator) {
        log.info("processFileWithCSVReader called. path: {}, charset: {}, separator: {} ", path, charset, separator);
        List<String[]> csvRows = new ArrayList<>();
        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
            BufferedReader br = Files.newBufferedReader(path, charset);
            CSVReader csvReader = new CSVReaderBuilder(br).withCSVParser(parser).build();
            String[] csvRow = null;
            while ((csvRow = csvReader.readNext()) != null) {
                csvRows.add(csvRow);
                log.info("Processed row: {}", Arrays.toString(csvRow));
            }
        } catch (Exception e) {
            log.error("Error in processFileWithCSVReader: {}", e);
        }
        return csvRows;
    }

    private void processFile(Path path, Charset charset, String separator) {
        log.info("processFile called. path: {}, charset: {}, separator: {}", path, charset, separator);
        switch (separator) {
        case CSVConstant.SEPARATOR_SEMICOLON:
            processFileForOutPayHeader(path, charset);
            break;
        case CSVConstant.SEPARATOR_PIPE:
            processFileForPolicy(path, charset);
            break;
        default:
            break;
        }
    }

    private void processFileForOutPayHeader(Path path, Charset charset) {
        log.info("processFileForOutPayHeader called. path: {}, charset: {}", path, charset);
        List<String[]> csvRows = processFileWithCSVReader(path, charset, CSVConstant.SEPARATOR_SEMICOLON.charAt(0));
        try {
            StringBuilder sb = new StringBuilder();
            for (String[] csvRow : csvRows) {
                OutPayHeaderDTO dto = new OutPayHeaderDTO();
                OutPayHeaderMapper.updateDTO(dto, csvRow);
                log.info("After csvRow to dto mapping. dto: {}", dto);
                boolean saved = saveOutPayHeader(dto);
                if (!saved) {
                    sb.append(StringUtils.arrayToDelimitedString(csvRow, CSVConstant.SEPARATOR_SEMICOLON));
                    sb.append("\n");
                }
            }
            showUnsavedRowsContent(sb);
        } catch (Exception e) {
            log.error("Error in processFileForOutPayHeader: {}", e);
            addErrorMessage(IMPORT_FAILED + e);
        }
    }

    private void showUnsavedRowsContent(StringBuilder sb) {
        log.info("showUnsavedRowsContent called. sb: {}", sb);
        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
            String unsavedRowsContent = sb.toString();
            log.warn("Unsaved rows. unsavedRowsContent: {}", unsavedRowsContent);
            redirectAttributes.addFlashAttribute("unsavedRowsContent", unsavedRowsContent);
        }
    }

    private boolean saveOutPayHeader(OutPayHeaderDTO dto) {
        log.info("saveOutPayHeader called. dto: {}", dto);
        boolean saved = true;
        try {
            outPayHeaderService.save(dto);
        } catch (Exception e) {
            saved = false;
            log.error("Error in saveOutPayHeader: {}", e);
        }
        return saved;
    }

    private void processFileForPolicy(Path path, Charset charset) {
        log.info("processFileForPolicy called. path: {}, charset: {}", path, charset);
        List<String[]> csvRows = processFileWithCSVReader(path, charset, CSVConstant.SEPARATOR_PIPE.charAt(0));
        try {
            StringBuilder sb = new StringBuilder();
            for (String[] csvRow : csvRows) {
                PolicyDTO dto = new PolicyDTO();
                PolicyMapper.updateDTO(dto, csvRow);
                log.info("After csvRow to dto mapping. dto: {}", dto);
                boolean saved = savePolicy(dto);
                if (!saved) {
                    sb.append(StringUtils.arrayToDelimitedString(csvRow, CSVConstant.SEPARATOR_PIPE));
                    sb.append("\n");
                }
            }
            showUnsavedRowsContent(sb);
        } catch (Exception e) {
            log.error("Error in processFileForPolicy: {}", e);
            addErrorMessage(IMPORT_FAILED + e);
        }
    }

    private boolean savePolicy(PolicyDTO dto) {
        log.info("savePolicy called. dto: {}", dto);
        boolean saved = true;
        try {
            policyService.save(dto);
        } catch (Exception e) {
            saved = false;
            log.error("Error in savePolicy: {}", e);
        }
        return saved;
    }

}
