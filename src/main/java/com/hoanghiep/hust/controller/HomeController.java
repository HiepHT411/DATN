package com.hoanghiep.hust.controller;

import com.hoanghiep.hust.dto.CreatePartDto;
import com.hoanghiep.hust.dto.PartDto;
import com.hoanghiep.hust.entity.*;
import com.hoanghiep.hust.exception.ModelVerificationException;
import com.hoanghiep.hust.repository.PartDirectionsRepository;
import com.hoanghiep.hust.repository.PartRepo;
import com.hoanghiep.hust.service.*;
import com.hoanghiep.hust.utility.CommonMapper;
import com.hoanghiep.hust.utility.VerifierUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;

@Controller
public class HomeController {

    @Autowired
    private IPartService partService;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private Result result;

    @Autowired
    private IQuestionService questionService;

    @Autowired
    private IResultService resultService;

    @Autowired
    private IUnitTestService unitTestService;

    @Autowired
    private PartRepo partRepo;

    private Boolean submitted = false;

    @Autowired
    private PartDirectionsRepository partDirectionsRepository;
    @GetMapping("/")
    public String main() {
        return "home";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
    @GetMapping("/partList")
    @PreAuthorize("isAuthenticated()")
    public String getUnitTest(Model model, @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "50") int pageSize,
                              @RequestParam(value = "sortField", required = false, defaultValue = "id") String sortField,
                              @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {
        Page<Part> partPage = partService.getAllParts(pageNo, pageSize, sortField, sortDir);
        Page<PartDto> partDtos = partService.getAllNonNullParts(pageNo, pageSize, sortField, sortDir);

        model.addAttribute("listOfParts", partDtos.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", partDtos.getTotalPages());
        model.addAttribute("totalItems", partDtos.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "partList";
    }

    @GetMapping("/deletePart/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePart(@PathVariable(value = "id") long id) {

        this.partService.deletePartById(id);
        return "redirect:/partList";
    }

    @GetMapping("/showFormForUpdatePart/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showFormForUpdatePart(@PathVariable(value = "id") long id, Model model) {
        Part part = partService.getPartById(id);

        model.addAttribute("part", part);
        return "updatePart";
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updatePart(@PathVariable("id") long id, @Valid Part part,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            part.setId(id);
            return "updatePart";
        }
        Part partToUpDate = partService.getPartById(id);
        if (Objects.nonNull(part.getPartNumber())) {
            partToUpDate.setPartNumber(part.getPartNumber());
        }
        if (Objects.nonNull(part.getPartType())) {
            partToUpDate.setPartType(part.getPartType());
        }
        if (Objects.nonNull(part.getDescription())) {
            partToUpDate.setDescription(part.getDescription());
        }
        if (Objects.nonNull(part.getPartNumber())) {
            partToUpDate.setTimes(part.getTimes());
        }
        partRepo.save(partToUpDate);
        return "redirect:/partList";
    }

    @GetMapping("part/{id}")
    @PreAuthorize("isAuthenticated()")
    public String startPart(@PathVariable(name = "id") Long id, Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (!(authentication instanceof AnonymousAuthenticationToken)) {
//            result.setUsername(authentication.getName());
//        }
        submitted = false;

        Part part = partService.getPartById(id);
        PartDto partDto = commonMapper.convertToResponse(part, PartDto.class);
        partDto.setYear(part.getUnitTest().getYear());
        partDto.setUnitTestNumber(part.getUnitTest().getUnitTestNumber());
        model.addAttribute("part", partDto);
        String direction = partDirectionsRepository.findById((long) part.getPartNumber()).isPresent() ? partDirectionsRepository.findById((long) part.getPartNumber()).get().getDirections() : "";
        model.addAttribute("directions", direction);


        return "startPart";
    }

    @PostMapping("/submit/part/{id}")
    @PreAuthorize("isAuthenticated()")
    public String submit(@ModelAttribute Part submitPart, @PathVariable(name = "id") Long id, Model m) {
        Part partInfo = null;
        if(!submitted) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = "";
            if (principal instanceof UserDetails) {
                username = ((UserDetails)principal).getUsername();
            } else {
                username = principal.toString();
            }
            partInfo = partService.getPartById(id);
            result.setUnitTestNumber(partInfo.getUnitTest().getUnitTestNumber());
            result.setYear(partInfo.getUnitTest().getYear());
            result.setPartNumber(partInfo.getPartNumber());
            result.setTotalCorrect(questionService.getResult(submitPart.getQuestions()));
            result.setUsername(username);
            questionService.saveScore(result);
            submitted = true;
        }
        //part = partService.getPartById(id);
        PartDto partDto = commonMapper.convertToResponse(partInfo, PartDto.class);
        partDto.setYear(partInfo.getUnitTest().getYear());
        partDto.setUnitTestNumber(partInfo.getUnitTest().getUnitTestNumber());
        m.addAttribute("part", partDto);
        m.addAttribute("result", result);
        return "result.html";
    }

    @GetMapping("/score")
    public String score(Model model, @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                        @RequestParam(value = "sortField", required = false, defaultValue = "totalCorrect") String sortField,
                        @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        Page<ResultTest> resultTestPage = resultService.getTopScoreUnitTest(pageNo, pageSize, "totalPoint", sortDir);
        model.addAttribute("sTList", resultTestPage.getContent());

        Page<Result> resultPage = resultService.getTopScorePart(pageNo, pageSize, sortField, sortDir);
        model.addAttribute("sPList", resultPage.getContent());

        model.addAttribute("listOfParts", resultPage.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", resultPage.getTotalPages());
        model.addAttribute("totalItems", resultPage.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "scoreboard.html";
    }


    @GetMapping(value = "/examList")
    @PreAuthorize("isAuthenticated()")
    public String getListOfMockExam(Model model, @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
                                    @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                    @RequestParam(value = "sortField", required = false, defaultValue = "id") String sortField,
                                    @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        Page<UnitTest> pageUnitTest = unitTestService.getAllUnitTests(pageNo, pageSize, sortField, sortDir);
        if(!pageUnitTest.getContent().isEmpty()) {
            model.addAttribute("listOfUnitTests", pageUnitTest.getContent());
        }
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", pageUnitTest.getTotalPages());
        model.addAttribute("totalItems", pageUnitTest.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "mockExams";
    }

    @GetMapping(value = "/createPart")
    @PreAuthorize("hasRole('ADMIN')")
    public String newPart(Map<String, Object> model) {
        return "createPart";
    }

    @PostMapping(value = "/createPart")
    @PreAuthorize("hasRole('ADMIN')")
    public String newPart(@Valid CreatePartDto createPartDto, @RequestParam("audio") MultipartFile audio, BindingResult result,
                          Map<String, Object> model) {
        Part newPart;

        try {
            VerifierUtils.verifyModelResult(result);
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = "";
            if (principal instanceof UserDetails) {
                username = ((UserDetails)principal).getUsername();
            } else {
                username = principal.toString();
            }
            newPart = partService.createPart(createPartDto, username, audio);
        } catch (ModelVerificationException e) {
            return "createPart";
        }
           return "home";
//        return "redirect:/editPart/" + newPart.getId();
    }

    @GetMapping(value = "/toeictips")
    public String toeicTips() {
        return "toeictips.html";
    }

    @GetMapping(value = "/test/audio")
    public String testAudio(){
        return "startListeningPart";
    }
}
