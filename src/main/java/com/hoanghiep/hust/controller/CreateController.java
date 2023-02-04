//package com.hoanghiep.hust.controller;
//
//import com.hoanghiep.hust.entity.Part;
//import com.hoanghiep.hust.entity.ResultTest;
//import com.hoanghiep.hust.entity.UnitTest;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//import java.util.List;
//import java.util.Objects;
//
//@Controller
//@RequestMapping("/create")
//public class CreateController {
//
//    private boolean submitted = false;
//
//    @GetMapping
//    public String initCreatePart(@PathVariable Long id, @PathVariable int partNumber,
//                                   Model model,
//                                   HttpServletRequest request){
//        submitted = false;
//        UnitTest unitTest = new UnitTest();
//        List<Part> parts = partService.getPartByUnitTestId(unitTest.getId());
//        model.addAttribute("numberOfParts", unitTest.getParts().size());
//        model.addAttribute("unitTest", unitTest);
//        model.addAttribute("part", parts.get(partNumber-1));
//
//        HttpSession mySession = request.getSession(true);
//        if (mySession == null) {
//            return this.error(model,request,"INITIATE: no session passed with request. Exiting now.");
//        }
//
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username = "";
//        if (principal instanceof UserDetails) {
//            username = ((UserDetails)principal).getUsername();
//        } else {
//            username = principal.toString();
//        }
//        resultTest.setUnitTestNumber(unitTest.getUnitTestNumber());
//        resultTest.setYear(unitTest.getYear());
//        resultTest.setUsername(username);
//
//        mySession.setAttribute("part", parts.get(partNumber-1));
//        mySession.setAttribute("resultTest", resultTest);
//        return this.displayAddQuestionForm(model,request);
//    }
//
//
//    @RequestMapping("/displayaddquestionform")
//    public String displayAddQuestionForm(Model aModel, HttpServletRequest request) {
//        Part part = (Part) request.getSession().getAttribute("part");
//        // IF THIS PART IS DONE, RETURN THE RESULTS
//        UnitTest unitTest = part.getUnitTest();
//        aModel.addAttribute("numberOfParts", unitTest.getParts().size());
//        aModel.addAttribute("unitTest", unitTest);
//        // ADD COMPONENTS TO THE MODEL
//        aModel.addAttribute("part", part);
//        return "startUnitTest";  // HTML TEMPLATE THAT DISPLAYS QUESTION DATA
//    } // QUIZQUESTION(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)
//
//    @RequestMapping("/nextQuestion ")
//    public String nextQuestion(Model aModel, HttpServletRequest request, @ModelAttribute Part donePart) {
//        ResultTest resultTest1 = (ResultTest) request.getSession().getAttribute("resultTest");
//
////        Part part= (Part) request.getSession().getAttribute("part");
//        Part partInfo = partService.getPartById(donePart.getId());
//        if(Objects.nonNull(donePart.getQuestions())) {
//            switch (partInfo.getPartNumber()) {
//                case 1:
//                    resultTest1.setPart1Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 2:
//                    resultTest1.setPart2Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 3:
//                    resultTest1.setPart3Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 4:
//                    resultTest1.setPart4Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 5:
//                    resultTest1.setPart5Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 6:
//                    resultTest1.setPart6Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                case 7:
//                    resultTest1.setPart7Point(questionService.getResult(donePart.getQuestions()));
//                    break;
//                default:
//                    break;
//            }
//        }
//        List<Part> parts = partService.getPartByUnitTestId(partInfo.getUnitTest().getId());
//        if (partInfo.getPartNumber() < parts.size()) {
//            Part nextPart = parts.get(partInfo.getPartNumber());
//            HttpSession mySession = request.getSession(true);
//            mySession.setAttribute("part", nextPart);
//            return this.displayAddQuestionForm(aModel,request);
//        } // IF(NOT DONE)
//        resultTest1.setTotalPoint(resultTest1.getPart1Point()+resultTest1.getPart2Point()+resultTest1.getPart3Point()+resultTest1.getPart4Point()+resultTest1.getPart5Point()+resultTest1.getPart6Point()+resultTest1.getPart7Point());
//        aModel.addAttribute("resultTest", resultTest1);
//        resultTestRepository.save(resultTest1);
//        submitted = true;
//        return "partList.html";
//    }
//
//}
