package com.hoanghiep.hust.controller;

import com.hoanghiep.hust.exception.AudioException;
import com.hoanghiep.hust.utility.CSVReaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/import")
public class ImportController {

    private final Path rootLocation = Path.of("src\\main\\resources\\static\\importfiles");

    @Autowired
    private CSVReaderUtils csvReaderUtils;

    @GetMapping("/csvfile")
    @PreAuthorize("hasRole('ADMIN')")
    public String getImportPage() throws IOException {
        return "importCsv";
    }

    @PostMapping("/csvfile")
    @PreAuthorize("hasRole('ADMIN')")
    public String importQuestion(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        int count = 0;
        try {
            if (file.isEmpty()) {
                throw new AudioException("Failed to import empty file.");
            }
            Files.createDirectories(rootLocation);
            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new AudioException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
            count = csvReaderUtils.readQuestionFromCSV(destinationFile.toString()).size();
        }
        catch (IOException e) {
            throw new AudioException("Failed to import file.", e);
        }

        redirectAttributes.addFlashAttribute("message",
                "You have successfully imported " + count  + " question(s)!");

//        ModelAndView mav = new ModelAndView();
//        mav.addObject("header", "Done");
//        mav.addObject("subheader", "Imported Successfully");
//        mav.setViewName("simplemessage");
        return "redirect:/import/csvfile";
    }
}
