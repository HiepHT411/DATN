package com.hoanghiep.hust.controller;

import com.hoanghiep.hust.entity.Part;
import com.hoanghiep.hust.entity.ResultTest;
import com.hoanghiep.hust.entity.UnitTest;
import com.hoanghiep.hust.service.IPartService;
import com.hoanghiep.hust.service.IQuestionService;
import com.hoanghiep.hust.service.IUnitTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

@Controller
@SessionAttributes({TestController.TEST_SESSION})
public class TestController {

    static final String TEST_SESSION = "testsession";
    private boolean submitted = false;

    @Autowired
    private ResultTest result;

    @Autowired
    private IPartService partService;

    @Autowired
    private IUnitTestService unitTestService;

    @Autowired
    private IQuestionService questionService;

    @DeleteMapping("/deletePart/{id}")
    public String deleteEmployee(@PathVariable(value = "id") long id) {

        this.partService.deletePartById(id);
        return "redirect:/";
    }

    @GetMapping("/unitTest/{id}/part/{partNumber}")
    public String getUnitTestById(@PathVariable Long id, @PathVariable int partNumber,
                                  @ModelAttribute Part donePart,
                                  Model model, RedirectAttributes redirectAttributes,
                                  HttpSession httpSession){
        submitted = false;
        ResultTest result = (ResultTest) httpSession.getAttribute("result");
        UnitTest unitTest = unitTestService.getUnitTestById(id);
        List<Part> parts = partService.getPartByUnitTestId(unitTest.getId());
        model.addAttribute("numberOfParts", unitTest.getParts().size());
        model.addAttribute("unitTest", unitTest);
        model.addAttribute("part", parts.get(partNumber-1));
        if(Objects.nonNull(donePart.getQuestions())) {
            switch (donePart.getPartNumber()) {
                case 1:
                    System.err.println("part1: ");
                    result.setPart1Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 2:
                    System.err.println("part2: ");
                    result.setPart2Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 3:
                    System.err.println("part3: ");
                    result.setPart3Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 4:
                    result.setPart4Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 5:
                    result.setPart5Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 6:
                    result.setPart6Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 7:
                    result.setPart7Point(questionService.getResult(donePart.getQuestions()));
                    break;
                default:
                    break;
            }
        }
        httpSession.setAttribute("result", result);
        if(partNumber == 1){
            return "/startUnitTest";
        }
//        redirectAttributes.addFlashAttribute("part", donePart);
        return "redirect:/unitTest/"+id+"/part/"+(partNumber);
    }

    @PostMapping("/submit/unitTest/{id}")
    @PreAuthorize("isAuthenticated()")
    public String submitUnitTest(@ModelAttribute Part donePart, @PathVariable(name = "id") Long id, Model m) {
        UnitTest unitTest = null;
        if(!submitted) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = "";
            if (principal instanceof UserDetails) {
                username = ((UserDetails)principal).getUsername();
            } else {
                username = principal.toString();
            }
            unitTest = unitTestService.getUnitTestById(id);
            result.setUnitTestNumber(unitTest.getUnitTestNumber());
            result.setYear(unitTest.getYear());
            result.setUsername(username);
            switch (donePart.getPartNumber()){
                case 1:
                    result.setPart1Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 2:
                    result.setPart2Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 3:
                    result.setPart3Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 4:
                    result.setPart4Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 5:
                    result.setPart5Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 6:
                    result.setPart6Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 7:
                    result.setPart7Point(questionService.getResult(donePart.getQuestions()));
                    break;
                default:
                    break;
            }
            result.setTotalPoint(result.getPart1Point()+result.getPart2Point()+ result.getPart3Point()
                        +result.getPart4Point()+ result.getPart5Point()+ result.getPart6Point()+ result.getPart7Point());
            questionService.saveUnitTestResult(result);
            submitted = true;
        }
        //part = partService.getPartById(id);
//        PartDto partDto = commonMapper.convertToResponse(partInfo, PartDto.class);
//        partDto.setYear(partInfo.getUnitTest().getYear());
//        partDto.setUnitTestNumber(partInfo.getUnitTest().getUnitTestNumber());
//        m.addAttribute("part", partDto);
//        m.addAttribute("result", result);
        return "result.html";
    }
}
