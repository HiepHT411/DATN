package com.hoanghiep.hust.controller;

import com.hoanghiep.hust.dto.Chose;
import com.hoanghiep.hust.dto.CreateQuestionDto;
import com.hoanghiep.hust.entity.*;
import com.hoanghiep.hust.repository.QuestionStackDirectionsRepository;
import com.hoanghiep.hust.repository.ResultTestRepository;
import com.hoanghiep.hust.service.IPartService;
import com.hoanghiep.hust.service.IQuestionService;
import com.hoanghiep.hust.service.IUnitTestService;
import com.hoanghiep.hust.service.S3StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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

    @Autowired
    private S3StorageService s3service;

    @GetMapping("/selectUnitTestAndPart")
    @PreAuthorize("hasRole('ADMIN')")
    public String selectUnitTestAndPart(Model model){
        model.addAttribute("unitTestOptions", unitTestService.getAllUnitTests(1, 10, "id", "desc").getContent());
        model.addAttribute("partOptions", Arrays.asList(1,2,3,4,5,6,7));
        model.addAttribute("chose", new Chose());
        return "selectUnitTestAndPart";
    }

    @PostMapping("/unitTest/part")
    @PreAuthorize("hasRole('ADMIN')")
    public String selectUnitTest(@ModelAttribute Chose chose, Model model){
        if (Objects.nonNull(partService.getPartByUnitTestIdAndPartNumber(chose.getUnitTestId(), chose.getPartNumber()))) {
            return "redirect:/addQuestion/unitTest/"+ chose.getUnitTestId()+"/part/"+chose.getPartNumber();
        }
        return "createPart";
    }
    @GetMapping("/selectPart")
    public List<Part> selectPart(@RequestParam(value = "unitTestId", required = true) Long unitTestId){
        return partService.getPartByUnitTestId(unitTestId);
    }



    @GetMapping("/unitTest/{id}/part/{partNumber}")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public String displayAddQuestionForm(Model aModel, HttpServletRequest request) {
        Part part = (Part) request.getSession().getAttribute("part");
        // IF THIS PART IS DONE, RETURN THE RESULTS
        UnitTest unitTest = (UnitTest) request.getSession().getAttribute("unitTest");
        if (questionService.getNumberOfQuestionOfPart(part) >= part.getNumberOfQuestions()) {
            aModel.addAttribute("header", "Done");
            aModel.addAttribute("subheader", "Reached maximum number of questions of this part");
            return "simplemessage";
        }
//        aModel.addAttribute("numberOfParts", unitTest.getParts().size());
        aModel.addAttribute("unitTest", unitTest);
        // ADD COMPONENTS TO THE MODEL
        aModel.addAttribute("part", part);
        return "addQuestion";
    }

    @PostMapping("/nextQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    public String nextQuestion(Model aModel, HttpServletRequest request, CreateQuestionDto questionDto, @RequestParam("image") MultipartFile file) throws IOException {
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
            if (Objects.nonNull(file) && !file.isEmpty()) {
//                StringBuilder fileNames = new StringBuilder();
//                Path fileNameAndPath = Paths.get("src\\main\\resources\\static\\images", file.getOriginalFilename());
//                fileNames.append(file.getOriginalFilename());
//                Files.write(fileNameAndPath, file.getBytes());
//                question.setImage("/images/"+fileNames);
            }
            String imageLink = s3service.uploadFile(file);
            question.setImage("https://toeicappstorage.s3.ap-southeast-2.amazonaws.com/" + imageLink);
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
