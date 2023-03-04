package com.hoanghiep.hust.controller;

import com.hoanghiep.hust.entity.Part;
import com.hoanghiep.hust.entity.UnitTest;
import com.hoanghiep.hust.service.IPartService;
import com.hoanghiep.hust.service.IUnitTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

@Controller
@Slf4j
public class AudioController {

    @Autowired
    private IPartService partService;

    @Autowired
    private IUnitTestService unitTestService;

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
        String file = "E:\\DA\\hust-elearning-english\\src\\main\\resources\\static\\audios/test-audio.mp3";

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
        String file = "E:\\DA\\hust-elearning-english\\src\\main\\resources\\static\\audios/";
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
}
