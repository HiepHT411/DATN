package com.hoanghiep.hust.controller;

import com.hoanghiep.hust.entity.Part;
import com.hoanghiep.hust.entity.ResultTest;
import com.hoanghiep.hust.entity.UnitTest;
import com.hoanghiep.hust.repository.PartDirectionsRepository;
import com.hoanghiep.hust.repository.ResultTestRepository;
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
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
//@SessionAttributes({TestController.ATTRIBUTE_NAME})
public class TestController {

//    static final String ATTRIBUTE_NAME = "testsession";
//    static final String BINDING_RESULT_NAME = "org.springframework.validation.BindingResult." + ATTRIBUTE_NAME;

    private boolean submitted = false;

    private ResultTest resultTest = new ResultTest();

//    @Autowired
//    private ResultTest result;

    @Autowired
    private IPartService partService;

    @Autowired
    private IUnitTestService unitTestService;

    @Autowired
    private IQuestionService questionService;

    @Autowired
    private ResultTestRepository resultTestRepository;

    @Autowired
    private PartDirectionsRepository partDirectionsRepository;


//    @GetMapping("/removethis/unitTest/{id}/part/{partNumber}")
//    public String getUnitTestById(@PathVariable Long id, @PathVariable int partNumber,
//                                        @ModelAttribute Part donePart,
//                                        Model model,
////                                        @ModelAttribute(ATTRIBUTE_NAME) ResultTest result,
//                                        RedirectAttributes redirectAttributes,
//                                        HttpSession httpSession){
//        submitted = false;
////        ResultTest result = (ResultTest) httpSession.getAttribute("result");
//        UnitTest unitTest = unitTestService.getUnitTestById(id);
//        List<Part> parts = partService.getPartByUnitTestId(unitTest.getId());
//        model.addAttribute("numberOfParts", unitTest.getParts().size());
//        model.addAttribute("unitTest", unitTest);
//        model.addAttribute("part", parts.get(partNumber-1));
////        if(Objects.nonNull(donePart.getQuestions())) {
//        if(!model.containsAttribute(BINDING_RESULT_NAME)) {
//            ResultTest newResultTest = new ResultTest();
//            model.addAttribute(ATTRIBUTE_NAME, newResultTest);
//        } else if(model.containsAttribute(BINDING_RESULT_NAME)){
//            switch (donePart.getPartNumber()) {
//                case 1:
//                    System.err.println("part1: ");
//                    result.setPart1Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 2:
//                    System.err.println("part2: ");
//                    result.setPart2Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 3:
//                    System.err.println("part3: ");
//                    result.setPart3Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 4:
//                    result.setPart4Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 5:
//                    result.setPart5Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 6:
//                    result.setPart6Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 7:
//                    result.setPart7Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                default:
//                    break;
//            }
//        }
//        httpSession.setAttribute("result", result);
//        redirectAttributes.addFlashAttribute(ATTRIBUTE_NAME, result);
////        final ModelAndView modelAndView = new ModelAndView("/startUnitTest");
//        if(partNumber == 1){
//            return "/startUnitTest";
//        }
//
//        return "redirect:/unitTest/"+id+"/part/"+(partNumber);
//    }


    @GetMapping("/unitTest/{id}/part/{partNumber}")
    @PreAuthorize("isAuthenticated()")
    public String getUnitTestById2(@PathVariable Long id, @PathVariable int partNumber,
                                   Model model,
                                   HttpServletRequest request) throws ParseException {
        submitted = false;
        UnitTest unitTest = unitTestService.getUnitTestById(id);
        List<Part> parts = partService.getPartByUnitTestId(unitTest.getId());
        parts.sort(Comparator.comparing(Part::getPartNumber));
        model.addAttribute("numberOfParts", unitTest.getParts().size());
        model.addAttribute("unitTest", unitTest);
        model.addAttribute("part", parts.get(partNumber-1));

        HttpSession mySession = request.getSession(true);
        if (mySession == null) {
            return this.error(model,request,"INITIATE: no session passed with request. Exiting now.");
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        resultTest.setUnitTestNumber(unitTest.getUnitTestNumber());
        resultTest.setYear(unitTest.getYear());
        resultTest.setUsername(username);

        mySession.setAttribute("part", parts.get(partNumber-1));
        mySession.setAttribute("resultTest", resultTest);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.MINUTE, 120);
        if(calendar.get(Calendar.AM_PM) == Calendar.PM) {
            calendar.add(Calendar.HOUR, 12);
        }
        Date endDate = calendar.getTime();
        mySession.setAttribute("endDate", endDate);
//        mySession.setAttribute("endDate", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-02-27 02:00:10"));
        return this.displayquestion(model,request);
    }


    @RequestMapping("/displayquestion")
    public String displayquestion(Model aModel, HttpServletRequest request) {
        Part part = (Part) request.getSession().getAttribute("part");
        // IF THIS PART IS DONE, RETURN THE RESULTS
        UnitTest unitTest = part.getUnitTest();
        aModel.addAttribute("numberOfParts", unitTest.getParts().size());
        aModel.addAttribute("unitTest", unitTest);
        // ADD COMPONENTS TO THE MODEL
        aModel.addAttribute("part", part);
        String direction = partDirectionsRepository.findById((long) part.getPartNumber()).isPresent() ? partDirectionsRepository.findById((long) part.getPartNumber()).get().getDirections() : "";
        aModel.addAttribute("directions", direction);
        Date endDate = (Date) request.getSession().getAttribute("endDate");
        aModel.addAttribute("endDate", endDate);
        return "startUnitTest";  // HTML TEMPLATE THAT DISPLAYS QUESTION DATA
    } // QUIZQUESTION(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/nextPart")
    @PreAuthorize("isAuthenticated()")
    public String nextPart(Model aModel, HttpServletRequest request, @ModelAttribute Part donePart) {
        ResultTest resultTest1 = (ResultTest) request.getSession().getAttribute("resultTest");

//        Part part= (Part) request.getSession().getAttribute("part");
        Part partInfo = partService.getPartById(donePart.getId());
        if(Objects.nonNull(donePart.getQuestions())) {
            switch (partInfo.getPartNumber()) {
                case 1:
                    resultTest1.setPart1Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 2:
                    resultTest1.setPart2Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 3:
                    resultTest1.setPart3Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 4:
                    resultTest1.setPart4Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 5:
                    resultTest1.setPart5Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 6:
                    resultTest1.setPart6Point(questionService.getResult(donePart.getQuestions()));
                    break;
                case 7:
                    resultTest1.setPart7Point(questionService.getResult(donePart.getQuestions()));
                    break;
                default:
                    break;
            }
        }
        List<Part> parts = partService.getPartByUnitTestId(partInfo.getUnitTest().getId());
        if (partInfo.getPartNumber() < parts.size()) {
            Part nextPart = partService.getPartByUnitTestIdAndPartNumber(partInfo.getUnitTest().getId(), partInfo.getPartNumber()+1);
            HttpSession mySession = request.getSession(true);
            mySession.setAttribute("part", nextPart);
            return this.displayquestion(aModel,request);
        } // IF(NOT DONE)
        resultTest1.setTotalPoint(resultTest1.getPart1Point()+resultTest1.getPart2Point()+resultTest1.getPart3Point()+resultTest1.getPart4Point()+resultTest1.getPart5Point()+resultTest1.getPart6Point()+resultTest1.getPart7Point());
        Date date = new Date();
        resultTest1.setDateTime(date.toString());
        aModel.addAttribute("resultTest", resultTest1);
        resultTestRepository.save(resultTest1);
        submitted = true;
        return "resultTest.html";
    }

//    @PostMapping("/submit/unitTest/{id}")
//    @PreAuthorize("isAuthenticated()")
//    public String submitUnitTest(@ModelAttribute Part donePart, @PathVariable(name = "id") Long id, Model m,
//                                 @ModelAttribute(ATTRIBUTE_NAME) ResultTest result, SessionStatus sessionStatus) {
//        UnitTest unitTest = null;
//        if(!submitted) {
//            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            String username = "";
//            if (principal instanceof UserDetails) {
//                username = ((UserDetails)principal).getUsername();
//            } else {
//                username = principal.toString();
//            }
//            unitTest = unitTestService.getUnitTestById(id);
//            result.setUnitTestNumber(unitTest.getUnitTestNumber());
//            result.setYear(unitTest.getYear());
//            result.setUsername(username);
//            switch (donePart.getPartNumber()){
//                case 1:
//                    result.setPart1Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 2:
//                    result.setPart2Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 3:
//                    result.setPart3Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 4:
//                    result.setPart4Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 5:
//                    result.setPart5Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 6:
//                    result.setPart6Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 7:
//                    result.setPart7Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                default:
//                    break;
//            }
//            result.setTotalPoint(result.getPart1Point()+result.getPart2Point()+ result.getPart3Point()
//                        +result.getPart4Point()+ result.getPart5Point()+ result.getPart6Point()+ result.getPart7Point());
//            questionService.saveUnitTestResult(result);
//            submitted = true;
//            sessionStatus.setComplete();
//        }
//        //part = partService.getPartById(id);
////        PartDto partDto = commonMapper.convertToResponse(partInfo, PartDto.class);
////        partDto.setYear(partInfo.getUnitTest().getYear());
////        partDto.setUnitTestNumber(partInfo.getUnitTest().getUnitTestNumber());
////        m.addAttribute("part", partDto);
////        m.addAttribute("result", result);
//        return "resultTest.html";
//    }

    @GetMapping("/error")
    public String error(Model aModel, HttpServletRequest request, String errorMessage) {
        aModel.addAttribute("err", errorMessage);
//        aModel.addAttribute("today", new Date().toString());
        return "error";
    }
}
