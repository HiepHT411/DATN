package com.hoanghiep.hust.controller;

import com.hoanghiep.hust.entity.Part;
import com.hoanghiep.hust.entity.UnitTest;
import com.hoanghiep.hust.service.IAudioService;
import com.hoanghiep.hust.service.IPartService;
import com.hoanghiep.hust.service.IUnitTestService;
import com.hoanghiep.hust.service.S3StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

@Controller
@Slf4j
public class AudioController {

    @Autowired
    private IPartService partService;

    @Autowired
    private IUnitTestService unitTestService;

    @Autowired
    private IAudioService audioService;

    @Autowired
    private S3StorageService storageService;

//    @RequestMapping(value = "/recfile/{id}", method = RequestMethod.GET,
//            produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
//    public HttpEntity<byte[]> downloadRecipientFile(@PathVariable("id") int id,
//                                                    ModelMap model, HttpServletResponse response) throws IOException,
//            ServletException {
//        log.debug("[GroupListController downloadRecipientFile]");
//        VoiceAudioLibrary dGroup = audioClipService.findAudioClip(id);
//        if (dGroup == null || dGroup.getAudioData() == null
//                || dGroup.getAudioData().length <= 0) {
//            throw new ServletException("No clip found/clip has not data, id="
//                    + id);
//        }
//        HttpHeaders header = new HttpHeaders();
////        I tried this too
//        //header.setContentType(new MediaType("audio", "mp3"));
//        header.setContentType(new MediaType("audio", "vnd.wave");
//        header.setContentLength(dGroup.getAudioData().length);
//        return new HttpEntity<byte[]>(dGroup.getAudioData(), header);
//    }

    @RequestMapping(value = "/audio/get/{characterId}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public ResponseEntity playAudio(HttpServletRequest request, HttpServletResponse response, @PathVariable("characterId") int characterId) throws FileNotFoundException {

        log.debug("[downloadRecipientFile]");

//        de.tki.chinese.entity.Character character = characterRepository.findById(characterId);
//        String file = UPLOADED_FOLDER + character.getSoundFile();
        String file = "src\\main\\resources\\static\\audios/test-audio.mp3";

        long length = new File(file).length();


        InputStreamResource inputStreamResource = new InputStreamResource( new FileInputStream(file));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(length);
        httpHeaders.setCacheControl(CacheControl.noCache().getHeaderValue());
        return new ResponseEntity(inputStreamResource, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/part/audio/{partId}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_OCTET_STREAM_VALUE })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity getPPartAudioFie(HttpServletRequest request, HttpServletResponse response, @PathVariable("partId") Long partId) throws FileNotFoundException {
        log.info("retrieve audio file of part {}", partId);
        Part part = partService.getPartById(partId);
        UnitTest unitTest = part.getUnitTest();
//        String file = "src\\main\\resources\\static\\audios/";
        String file = "src\\main\\resources\\static";
//        if (partId == 40) {
//            file += "test-audio.mp3";
//        }
        if (Objects.nonNull(part) && Objects.nonNull(unitTest)) {
            if (Objects.nonNull(part.getAudio())) {
                file += part.getAudio();
            } else {
                file += "\\audios/" + unitTest.getYear() + "-TEST" + unitTest.getUnitTestNumber() + "-PART" + part.getPartNumber() + ".mp3";
            }
        }
        long length = new File(file).length();

        InputStreamResource inputStreamResource = new InputStreamResource( new FileInputStream(file));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(length);
        httpHeaders.setCacheControl(CacheControl.noCache().getHeaderValue());
        return new ResponseEntity(inputStreamResource, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/uploaded/files/audio")
    @PreAuthorize("hasRole('ADMIN')")
    public String listUploadedFiles(Model model) throws IOException {

//        model.addAttribute("files", audioService.loadAll().map(
//                        path -> MvcUriComponentsBuilder.fromMethodName(AudioController.class,
//                                "serveFile", path.getFileName().toString()).build().toUri().toString())
//                .collect(Collectors.toList()));
        model.addAttribute("files", storageService.listAllUploadedFile());
        log.info("List all uploaded files");
        return "testUploadAudio";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
//        local
        log.info("Download file {} from local storage", filename);
        Resource file = audioService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/uploaded/files/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        byte[] data = storageService.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        log.info("Download file {}", fileName);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        log.info("delete file {}", fileName);
        return new ResponseEntity<>(storageService.deleteFile(fileName), HttpStatus.OK);
    }

    @PostMapping("/part/audio")
    @PreAuthorize("hasRole('ADMIN')")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

//        audioService.store(file);
//        redirectAttributes.addFlashAttribute("message",
//                "You successfully uploaded " + file.getOriginalFilename() + "!");
        String audioLink = storageService.uploadFile(file, "audiofiles");
        log.info("uploaded file to storage with link {}", audioLink);

        return "redirect:/uploaded/files/audio";
    }
}
