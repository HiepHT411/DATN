package com.hoanghiep.hust.controller;

import com.hoanghiep.hust.entity.Part;
import com.hoanghiep.hust.entity.ResultTest;
import com.hoanghiep.hust.entity.UnitTest;
import com.hoanghiep.hust.repository.PartDirectionsRepository;
import com.hoanghiep.hust.repository.ResultTestRepository;
import com.hoanghiep.hust.service.IPartService;
import com.hoanghiep.hust.service.IQuestionService;
import com.hoanghiep.hust.service.IUnitTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
//@SessionAttributes({TestController.ATTRIBUTE_NAME})
@Slf4j
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

        int lastPart = 1;
        for (Part p : parts) {
            if (p.getPartNumber() > lastPart)
                lastPart = p.getPartNumber();
        }
        model.addAttribute("lastPart", lastPart);

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
        mySession.setAttribute("lastPart", lastPart);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
//        if (calendar.get(Calendar.AM_PM) == Calendar.PM) {
//            calendar.add(Calendar.HOUR, 12);
//        }
//        calendar.add(Calendar.HOUR_OF_DAY, 2);
//        if(calendar.get(Calendar.AM_PM) == Calendar.PM && calendar.get(Calendar.HOUR_OF_DAY) >= 22) {
//            calendar.add(Calendar.HOUR_OF_DAY, 12);
//        }
//        Date endDate = calendar.getTime();

        Date endDate1 = new Date();
        Date endDate2 = Date.from(endDate1.toInstant().plus(Duration.ofMinutes(120)));
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss"); //EEE, d MMM yyyy HH:mm:ss
        String endDate = sdf.format(endDate2);
        mySession.setAttribute("endDate", endDate);
//        mySession.setAttribute("endDate", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-02-27 02:00:10"));
        log.info("User {} start unit test {} at {}", username, id, endDate1);
        return this.displayquestion(model,request);
    }


    @RequestMapping("/displayquestion")
    public String displayquestion(Model aModel, HttpServletRequest request) throws ParseException {
        Part part = (Part) request.getSession().getAttribute("part");
        // IF THIS PART IS DONE, RETURN THE RESULTS
        UnitTest unitTest = part.getUnitTest();
        aModel.addAttribute("numberOfParts", unitTest.getParts().size());
        aModel.addAttribute("unitTest", unitTest);
        // ADD COMPONENTS TO THE MODEL
        aModel.addAttribute("part", part);
        String direction = partDirectionsRepository.findById((long) part.getPartNumber()).isPresent() ? partDirectionsRepository.findById((long) part.getPartNumber()).get().getDirections() : "";
        aModel.addAttribute("directions", direction);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
        Date endDate = sdf.parse(request.getSession().getAttribute("endDate").toString());
        aModel.addAttribute("endDate", endDate);
        log.info("End date at {}", endDate);
        return "startUnitTest";  // HTML TEMPLATE THAT DISPLAYS QUESTION DATA
    }

    @RequestMapping("/nextPart")
    @PreAuthorize("isAuthenticated()")
    public String nextPart(Model aModel, HttpServletRequest request, @ModelAttribute Part donePart) throws ParseException {
        ResultTest resultTest1 = (ResultTest) request.getSession().getAttribute("resultTest");
        int lastPart = (Integer) request.getSession().getAttribute("lastPart");

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
            aModel.addAttribute("lastPart", lastPart);
            return this.displayquestion(aModel,request);
        } // IF(NOT DONE)
        resultTest1.setTotalPoint(resultTest1.getPart1Point()+resultTest1.getPart2Point()+resultTest1.getPart3Point()+resultTest1.getPart4Point()+resultTest1.getPart5Point()+resultTest1.getPart6Point()+resultTest1.getPart7Point());
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatDateTime = now.format(format);
        resultTest1.setDateTime(formatDateTime);
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

    @GetMapping("/showFormForUpdateUnitTest/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showFormForUpdateUnitTest(@PathVariable(value = "id") long id, Model model) {
        UnitTest unitTest = unitTestService.getUnitTestById(id);

        model.addAttribute("unitTest", unitTest);
        return "updateUnitTest";
    }

    @PostMapping("/updateUnitTest/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUnitTest(@PathVariable("id") long id, @Valid UnitTest unitTest,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            unitTest.setId(id);
            return "updateUnitTest";
        }
        unitTestService.updateUnitTest(id, unitTest);

        return "redirect:/examList";
    }

    @GetMapping("/deleteUnitTest/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUnitTest(@PathVariable(value = "id") long id) {

        this.unitTestService.deleteUnitTestById(id);
        return "redirect:/examList";
    }

    @GetMapping("/error")
    public String error(Model aModel, HttpServletRequest request, String errorMessage) {
        aModel.addAttribute("err", errorMessage);
//        aModel.addAttribute("today", new Date().toString());
        log.info("An error occurred: {}", errorMessage);
        return "error";
    }
}
