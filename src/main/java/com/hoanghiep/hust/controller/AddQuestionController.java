package com.hoanghiep.hust.controller;

import com.hoanghiep.hust.dto.CreateQuestionDto;
import com.hoanghiep.hust.entity.*;
import com.hoanghiep.hust.repository.QuestionStackDirectionsRepository;
import com.hoanghiep.hust.repository.ResultTestRepository;
import com.hoanghiep.hust.service.IPartService;
import com.hoanghiep.hust.service.IQuestionService;
import com.hoanghiep.hust.service.IUnitTestService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private QuestionStackDirectionsRepository questionStackDirectionsRepository;

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
    public String nextQuestion(Model aModel, HttpServletRequest request, @ModelAttribute CreateQuestionDto questionDto) {
        UnitTest unitTest = (UnitTest) request.getSession().getAttribute("unitTest");
        Part part= (Part) request.getSession().getAttribute("part");
        if(Objects.nonNull(questionDto)) {
            QuestionStackDirections questionStackDirections = null;
            if(Objects.nonNull(questionDto.getQuestionStackDirectionTitle()) && !questionDto.getQuestionStackDirectionTitle().isEmpty()
                    && Objects.nonNull(questionDto.getQuestionStackDirectionDirections()) && !questionDto.getQuestionStackDirectionDirections().isEmpty()){
                QuestionStackDirections newQuestionStackDirections = new QuestionStackDirections(questionDto.getQuestionStackDirectionTitle(), questionDto.getQuestionStackDirectionDirections());
                questionStackDirections = questionStackDirectionsRepository.save(newQuestionStackDirections);
            }
            Question question = Question.builder()
                    .index(questionDto.getIndex())
                    .title(questionDto.getTitle())
                    .ans(questionDto.getAns())
                    .chose(-1)
                    .optionA(questionDto.getOptionA())
                    .optionB(questionDto.getOptionB())
                    .optionC(questionDto.getOptionC())
                    .optionD(questionDto.getOptionD())
                    .part(part)
                    .build();
            if (Objects.nonNull(questionStackDirections)){
                question.setQuestionStackDirections(questionStackDirections);
            }
            questionService.saveQuestion(question);
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
