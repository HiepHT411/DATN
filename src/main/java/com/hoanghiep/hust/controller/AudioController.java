package com.hoanghiep.hust.controller;

import com.hoanghiep.hust.entity.Part;
import com.hoanghiep.hust.entity.UnitTest;
import com.hoanghiep.hust.service.IAudioService;
import com.hoanghiep.hust.service.IPartService;
import com.hoanghiep.hust.service.IUnitTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class AudioController {

    @Autowired
    private IPartService partService;

    @Autowired
    private IUnitTestService unitTestService;

    @Autowired
    private IAudioService audioService;

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
    public ResponseEntity getPPartAudioFie(HttpServletRequest request, HttpServletResponse response, @PathVariable("partId") Long partId) throws FileNotFoundException {

        Part part = partService.getPartById(partId);
        UnitTest unitTest = part.getUnitTest();
        String file = "src\\main\\resources\\static\\audios/";
//        if (partId == 40) {
//            file += "test-audio.mp3";
//        }
        if (Objects.nonNull(part) && Objects.nonNull(unitTest)) {
            file += unitTest.getYear() + "-TEST" + unitTest.getUnitTestNumber() + "-PART" + part.getPartNumber() + ".mp3";
        }
        long length = new File(file).length();

        InputStreamResource inputStreamResource = new InputStreamResource( new FileInputStream(file));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(length);
        httpHeaders.setCacheControl(CacheControl.noCache().getHeaderValue());
        return new ResponseEntity(inputStreamResource, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/uploaded/files/audio")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", audioService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(AudioController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "testUploadAudio";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = audioService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/part/audio")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        audioService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }
}
