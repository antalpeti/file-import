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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.MediaType;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.tika.detect.AutoDetectReader;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import hu.nn.constant.CSVConstant;
import hu.nn.dto.OutPayHeaderDTO;
import hu.nn.dto.PolicyDTO;
import hu.nn.dto.SurValuesDTO;
import hu.nn.enums.SurValuesEnum;
import hu.nn.mapper.OutPayHeaderMapper;
import hu.nn.mapper.PolicyMapper;
import hu.nn.mapper.SurValuesMapper;
import hu.nn.service.OutPayHeaderService;
import hu.nn.service.PolicyService;
import hu.nn.service.SurValuesService;
import hu.nn.util.CSVUtil;
import hu.nn.util.ExceptionUtil;
import hu.nn.util.StringUtil;
import hu.nn.util.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@ControllerAdvice
public class ImportController {

    @Autowired
    private OutPayHeaderService outPayHeaderService;
    @Autowired
    private PolicyService policyService;
    @Autowired
    private SurValuesService surValuesService;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    private static final String IMPORT_DIR = "./uploads/";
    private static final String REDIRECT = "redirect:/";
    private static final String SUCCESS = "success";
    private static final String WARNING = "warning";
    private static final String ERROR = "error";
    private static final String UNSAVED_ROWS_CONTENT = "unsavedRowsContent";
    private static final String CAUSES_OF_SAVE_FAILURE_CONTENT = "causesOfSaveFailureContent";

    private static final String IMPORT_FAILED = "Import failed: ";
    private static final String ROW_HAS_ALREADY_STORED = "Row has already stored.";

    private RedirectAttributes redirectAttributes;
    private static final Object lock = new Object();

    @GetMapping("/")
    public String homepage() {
        return "index";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleFileUploadError(RedirectAttributes attributes) {
        log.info("handleFileUploadError called.");
        redirectAttributes = attributes;
        addErrorMessage("You could not upload file bigger than " + maxFileSize);
        return REDIRECT;
    }

    private void addErrorMessage(String message) {
        log.info("addErrorMessage called. message: {}", message);
        redirectAttributes.addFlashAttribute(ERROR, message);
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        log.info("uploadFile called.");
        synchronized (lock) {
            log.info("lock acquired.");
            redirectAttributes = attributes;
            String userDir = System.getProperty("user.dir");
            Path path = Paths.get(userDir + IMPORT_DIR.substring(1));
            String originalFileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            log.info("File attributes. userDir: {}, path: {}, originalFileName: {}, contentType: {}", userDir, path, originalFileName, contentType);
            boolean fileProcessEnabled = isValidFile(file, path, originalFileName, contentType);
            if (fileProcessEnabled && originalFileName != null) {
                try {
                    String cleanedOriginalFileName = StringUtils.cleanPath(originalFileName);
                    path = Paths.get(IMPORT_DIR + cleanedOriginalFileName);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    Charset detectedCharset = detectCharset(path);
                    String detectedSeparator = detectSeparator(path, detectedCharset);
                    processFile(path, detectedCharset, detectedSeparator);
                    log.info("Successfull import. cleanedOriginalFileName: {}", cleanedOriginalFileName);
                    addSuccessMessage("Import successfull: " + cleanedOriginalFileName);
                } catch (Exception e) {
                    log.error("Error in uploadFile during file processing: {}", e);
                    addErrorMessage(IMPORT_FAILED + e);
                }
            }
        }
        log.info("lock released.");

        return REDIRECT;
    }

    private boolean isValidFile(MultipartFile file, Path path, String originalFileName, String contentType) {
        log.info("isValidFile called. file: {}, path: {}, originalFileName: {}, contentType: {}", file, path, originalFileName, contentType);
        Set<String> allowedExtensions = Set.of("csv", "txt");
        String extension = FileNameUtils.getExtension(originalFileName);
        boolean fileProcessEnabled = true;
        if (!Files.exists(path)) {
            String pathAsString = path.toString();
            log.warn("Missing directory. pathAsString: {}", pathAsString);
            addWarningMessage("Missing directory: " + pathAsString);
            fileProcessEnabled = false;
        } else if (file.isEmpty()) {
            log.warn("Empty file. originalFileName: {}", originalFileName);
            addWarningMessage("Empty file: " + originalFileName);
            fileProcessEnabled = false;
        } else if (!MediaType.TEXT_PLAIN.equals(contentType) && !allowedExtensions.contains(extension.toLowerCase())) {
            log.warn("Not plain text file and not allowed extension. contentType: {}, extension: {}", contentType, extension);
            addWarningMessage("Not plain text file: " + contentType + ". Not allowed extension" + allowedExtensions + ": " + extension);
            fileProcessEnabled = false;
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
        log.info("Detected separator. separator: {}", separator);
        return separator;
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
            processFileForSurValues(path, charset);
            break;
        }
    }

    private void processFileForOutPayHeader(Path path, Charset charset) {
        log.info("processFileForOutPayHeader called. path: {}, charset: {}", path, charset);
        List<String[]> csvRows = processFileWithCSVReader(path, charset, CSVConstant.SEPARATOR_SEMICOLON.charAt(0));
        try {
            StringBuilder unsavedRows = new StringBuilder();
            StringBuilder causesOfSaveFailure = new StringBuilder();
            List<OutPayHeaderDTO> rowsInDB = outPayHeaderService.findAll();
            for (String[] csvRow : csvRows) {
                OutPayHeaderDTO dto = new OutPayHeaderDTO();
                OutPayHeaderMapper.updateDTO(dto, csvRow);
                log.info("After csvRow to dto mapping. dto: {}", dto);
                boolean saved = false;
                if (rowsInDB.contains(dto)) {
                    dto.setCauseOfSaveFailure(ROW_HAS_ALREADY_STORED);
                } else {
                    saved = saveOutPayHeader(dto);
                }
                if (!saved) {
                    appendData(unsavedRows, causesOfSaveFailure, csvRow, dto.getCauseOfSaveFailure(), CSVConstant.SEPARATOR_SEMICOLON);
                }
            }
            showContent(UNSAVED_ROWS_CONTENT, unsavedRows);
            showContent(CAUSES_OF_SAVE_FAILURE_CONTENT, causesOfSaveFailure);
        } catch (Exception e) {
            log.error("Error in processFileForOutPayHeader: {}", e);
            addErrorMessage(IMPORT_FAILED + e);
        }
    }

    private List<String[]> processFileWithCSVReader(Path path, Charset charset, char separator) {
        log.info("processFileWithCSVReader called. path: {}, charset: {}, separator: {}", path, charset, separator);
        List<String[]> csvRows = new ArrayList<>();
        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
            BufferedReader br = Files.newBufferedReader(path, charset);
            CSVReader csvReader = new CSVReaderBuilder(br).withCSVParser(parser).build();
            String[] csvRow = null;
            while ((csvRow = csvReader.readNext()) != null) {
                log.info("Processed row. Arrays.asList(csvRow): {}", Arrays.asList(csvRow));
                if (CSVUtil.isEmpty(csvRow)) {
                    continue;
                }
                csvRows.add(csvRow);
            }
        } catch (Exception e) {
            log.error("Error in processFileWithCSVReader: {}", e);
        }
        return csvRows;
    }

    private boolean saveOutPayHeader(OutPayHeaderDTO dto) {
        log.info("saveOutPayHeader called. dto: {}", dto);
        boolean saved = true;
        try {
            outPayHeaderService.save(dto);
        } catch (Exception e) {
            log.error("Error in saveOutPayHeader: {}", e);
            saved = false;
            if (Util.isEmpty(dto.getCauseOfSaveFailure())) {
                dto.setCauseOfSaveFailure(ExceptionUtil.getRootCauseMessageWithoutLineFeed(e));
            }
        }
        return saved;
    }

    private void appendData(StringBuilder unsavedRows, StringBuilder causesOfSaveFailure, String[] csvRow, String causeOfSaveFailure, String separator) {
        log.info("appendData called. csvRow: {}, causeOfSaveFailure: {}, separator: {}", csvRow, causeOfSaveFailure, separator);
        unsavedRows.append(StringUtils.arrayToDelimitedString(csvRow, separator));
        unsavedRows.append("\n");
        causesOfSaveFailure.append(causeOfSaveFailure);
        causesOfSaveFailure.append("\n");
    }

    private void showContent(String attributeName, StringBuilder sb) {
        log.info("showContent called. sb: {}", sb);
        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
            String content = sb.toString();
            log.warn(attributeName + " content. content: {}", content);
            redirectAttributes.addFlashAttribute(attributeName, content);
        }
    }

    private void processFileForPolicy(Path path, Charset charset) {
        log.info("processFileForPolicy called. path: {}, charset: {}", path, charset);
        List<String[]> csvRows = processFileWithCSVReader(path, charset, CSVConstant.SEPARATOR_PIPE.charAt(0));
        try {
            StringBuilder unsavedRows = new StringBuilder();
            StringBuilder causesOfSaveFailure = new StringBuilder();
            List<PolicyDTO> rowsInDB = policyService.findAll();
            for (String[] csvRow : csvRows) {
                PolicyDTO dto = new PolicyDTO();
                PolicyMapper.updateDTO(dto, csvRow);
                log.info("After csvRow to dto mapping. dto: {}", dto);
                boolean saved = false;
                if (rowsInDB.contains(dto)) {
                    dto.setCauseOfSaveFailure(ROW_HAS_ALREADY_STORED);
                } else {
                    saved = savePolicy(dto);
                }
                if (!saved) {
                    appendData(unsavedRows, causesOfSaveFailure, csvRow, dto.getCauseOfSaveFailure(), CSVConstant.SEPARATOR_PIPE);
                }
            }
            showContent(UNSAVED_ROWS_CONTENT, unsavedRows);
            showContent(CAUSES_OF_SAVE_FAILURE_CONTENT, causesOfSaveFailure);
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
            log.error("Error in savePolicy: {}", e);
            saved = false;
            if (Util.isEmpty(dto.getCauseOfSaveFailure())) {
                dto.setCauseOfSaveFailure(ExceptionUtil.getRootCauseMessageWithoutLineFeed(e));
            }
        }
        return saved;
    }

    private void processFileForSurValues(Path path, Charset charset) {
        log.info("processFileForSurValues called. path: {}, charset: {}", path, charset);
        List<String[]> rows = processFileWithStringUtilAndCSVUtil(path, charset);
        try {
            StringBuilder unsavedRows = new StringBuilder();
            StringBuilder causesOfSaveFailure = new StringBuilder();
            List<SurValuesDTO> rowsInDB = surValuesService.findAll();
            for (String[] row : rows) {
                SurValuesDTO dto = new SurValuesDTO();
                SurValuesMapper.updateDTO(dto, row);
                log.info("After row to dto mapping. dto: {}", dto);
                boolean saved = false;
                if (rowsInDB.contains(dto)) {
                    dto.setCauseOfSaveFailure(ROW_HAS_ALREADY_STORED);
                } else {
                    saved = saveSurValues(dto);
                }
                if (!saved) {
                    appendData(unsavedRows, causesOfSaveFailure, row, dto.getCauseOfSaveFailure(), "");
                }
            }
            showContent(UNSAVED_ROWS_CONTENT, unsavedRows);
            showContent(CAUSES_OF_SAVE_FAILURE_CONTENT, causesOfSaveFailure);
        } catch (Exception e) {
            log.error("Error in processFileForSurValues: {}", e);
            addErrorMessage(IMPORT_FAILED + e);
        }
    }

    private List<String[]> processFileWithStringUtilAndCSVUtil(Path path, Charset charset) {
        log.info("processFileWithStringUtilAndCSVUtil called. path: {}, charset: {}", path, charset);
        try (Stream<String> lines = Files.lines(path, charset)) {
            return lines.map(this::processLine).filter(CSVUtil::isNotEmpty).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error in processFileWithStringUtilAndCSVUtil: {}", e);
        }
        return Collections.emptyList();
    }

    private String[] processLine(String line) {
        log.info("processLine called. line: {}", line);
        if (Util.isEmpty(line)) {
            return new String[0];
        }
        String[] row = new String[6];
        for (SurValuesEnum surValuesEnum : SurValuesEnum.values()) {
            row[surValuesEnum.getRowArrayIndex()] = StringUtil.getSubstring(line, surValuesEnum.getLineStartIndex(), surValuesEnum.getLineLength());
        }
        return row;
    }

    private boolean saveSurValues(SurValuesDTO dto) {
        log.info("saveSurValues called. dto: {}", dto);
        boolean saved = true;
        try {
            surValuesService.save(dto);
        } catch (Exception e) {
            log.error("Error in saveSurValues: {}", e);
            saved = false;
            if (Util.isEmpty(dto.getCauseOfSaveFailure())) {
                dto.setCauseOfSaveFailure(ExceptionUtil.getRootCauseMessageWithoutLineFeed(e));
            }
        }
        return saved;
    }

    private void addSuccessMessage(String message) {
        log.info("addSuccessMessage called. message: {}", message);
        redirectAttributes.addFlashAttribute(SUCCESS, message);
    }

}
