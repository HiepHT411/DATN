package com.hoanghiep.hust.controller;

import com.hoanghiep.hust.entity.Part;
import com.hoanghiep.hust.entity.Question;
import com.hoanghiep.hust.entity.ResultTest;
import com.hoanghiep.hust.entity.UnitTest;
import com.hoanghiep.hust.repository.ResultTestRepository;
import com.hoanghiep.hust.service.IPartService;
import com.hoanghiep.hust.service.IQuestionService;
import com.hoanghiep.hust.service.IUnitTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/addQuestion")
public class AddQuestionController {

    private boolean submitted = false;

    @Autowired
    private IPartService partService;

    @Autowired
    private IUnitTestService unitTestService;

    @Autowired
    private IQuestionService questionService;

    @Autowired
    private ResultTestRepository resultTestRepository;

    @GetMapping("/selectUnitTestAndPart")
    public String selectUnitTestAndPart(Model model){

        return "selectUnitTestAndPart";
    }

    @GetMapping("/selectUnitTest")
    public List<UnitTest> selectUnitTest(){
        return unitTestService.getAllUnitTests(1, 10, "id", "desc").getContent();
    }
    @GetMapping("/selectPart")
    public List<Part> selectPart(@RequestParam(value = "unitTestId", required = true) Long unitTestId){
        return partService.getPartByUnitTestId(unitTestId);
    }



    @GetMapping("/unitTest/{id}/part/{partNumber}")
    public String addQuestion(Model model, HttpServletRequest request,@PathVariable Long id, @PathVariable int partNumber){
        submitted = false;
        UnitTest unitTest = unitTestService.getUnitTestById(id);
        Part part = null;
        if(Objects.nonNull(unitTest)){
            part = partService.getPartByUnitTestIdAndPartNumber(unitTest.getId(), partNumber);
        }
//        model.addAttribute("numberOfQuestion", part.getQuestions().size());
        model.addAttribute("unitTest", unitTest);
        model.addAttribute("part", part);

        HttpSession mySession = request.getSession(true);
        if (mySession == null) {
            return this.error(model,request,"INITIATE: no session passed with request. Exiting now.");
        }
        mySession.setAttribute("unitTest", unitTest);
        mySession.setAttribute("part", part);
        return displayAddQuestionForm(model, request);
    }

    @RequestMapping("/displayaddquestionform")
    public String displayAddQuestionForm(Model aModel, HttpServletRequest request) {
        Part part = (Part) request.getSession().getAttribute("part");
        // IF THIS PART IS DONE, RETURN THE RESULTS
        UnitTest unitTest = (UnitTest) request.getSession().getAttribute("unitTest");

//        aModel.addAttribute("numberOfParts", unitTest.getParts().size());
        aModel.addAttribute("unitTest", unitTest);
        // ADD COMPONENTS TO THE MODEL
        aModel.addAttribute("part", part);
        return "addQuestion";
    }

    @RequestMapping("/nextQuestion")
    public String nextQuestion(Model aModel, HttpServletRequest request, @ModelAttribute Question questionDto) {
        UnitTest unitTest = (UnitTest) request.getSession().getAttribute("unitTest");
        Part part= (Part) request.getSession().getAttribute("part");
        if(Objects.nonNull(questionDto)) {
            questionDto.setPart(part);
            questionDto.setChose(-1);
            questionService.saveQuestion(questionDto);
        }
        aModel.addAttribute("unitTest", unitTest);
        aModel.addAttribute("part", part);
        submitted = true;
        return this.displayAddQuestionForm(aModel,request);
    }

    @GetMapping("/leave")
    public String leave(){
        return "home";
    }
    @GetMapping("/error")
    public String error(Model aModel, HttpServletRequest request, String errorMessage) {
        aModel.addAttribute("err", errorMessage);
        return "error";
    }
}
