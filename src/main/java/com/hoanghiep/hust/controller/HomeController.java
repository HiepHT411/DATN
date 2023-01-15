package com.hoanghiep.hust.controller;

import com.hoanghiep.hust.dto.PartDto;
import com.hoanghiep.hust.entity.*;
import com.hoanghiep.hust.exception.ModelVerificationException;
import com.hoanghiep.hust.security.AuthenticatedUser;
import com.hoanghiep.hust.service.IPartService;
import com.hoanghiep.hust.service.IQuestionService;
import com.hoanghiep.hust.service.IResultService;
import com.hoanghiep.hust.service.IUnitTestService;
import com.hoanghiep.hust.utility.CommonMapper;
import com.hoanghiep.hust.utility.VerifierUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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
    private Boolean submitted = false;

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
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                              @RequestParam(value = "sortField", required = false, defaultValue = "id") String sortField,
                              @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {
        Page<Part> partPage = partService.getAllParts(pageNo, pageSize, sortField, sortDir);
        Page<PartDto> pagePartDto = commonMapper.convertToResponsePage(partPage, PartDto.class, partPage.getPageable());
        for (int i = 0; i < partPage.getContent().size(); i++) {
            pagePartDto.getContent().get(i).setYear(partPage.getContent().get(i).getUnitTest().getYear());
            pagePartDto.getContent().get(i).setUnitTestNumber(partPage.getContent().get(i).getUnitTest().getUnitTestNumber());
        }
        model.addAttribute("listOfParts", pagePartDto.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", partPage.getTotalPages());
        model.addAttribute("totalItems", partPage.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "partList";
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
       Page<Result> resultPage = resultService.getTopScore(pageNo, pageSize, sortField, sortDir);
        model.addAttribute("sList", resultPage.getContent());

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
    @PreAuthorize("isAuthenticated()")
    public String newQuiz(Map<String, Object> model) {
        return "createPart";
    }

    @PostMapping(value = "/createPart")
    @PreAuthorize("isAuthenticated()")
    public String newQuiz(@AuthenticationPrincipal AuthenticatedUser user, @Valid Part part, BindingResult result,
                          Map<String, Object> model) {
        Part newPart;

        try {
            VerifierUtils.verifyModelResult(result);
            //newPart = quizService.save(part, user.getUser());
        } catch (ModelVerificationException e) {
            return "createQuiz";
        }
        return "error";
//        return "redirect:/editQuiz/" + newPart.getId();
    }
}
