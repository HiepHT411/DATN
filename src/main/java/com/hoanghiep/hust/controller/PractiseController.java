package com.hoanghiep.hust.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoanghiep.hust.dto.Practise;
import com.hoanghiep.hust.dto.PractiseQuestion;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

@Controller
public class PractiseController {

    @Autowired
    private AmazonS3 s3Client;

    @GetMapping("/practise")
    @SneakyThrows
    public String random20Questions(Model model) {
        ObjectMapper objectMapper = new ObjectMapper();

//        File data = new File("src/main/resources/toeicQuestions.json");

        S3Object s3object = s3Client.getObject("toeicapps3", "toeicQuestions.json");
        InputStream is = s3object.getObjectContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        List<PractiseQuestion> practiseQuestions =  objectMapper.readValue(br, new TypeReference<List<PractiseQuestion>>() {});
        Collections.shuffle(practiseQuestions);
        List<PractiseQuestion> randomQuestions = practiseQuestions.subList(0, 20);
        Practise practise = new Practise(randomQuestions);
        model.addAttribute("practise", practise);

        return "startPractising";
    }

    @PostMapping("/practise")
    public String submit(@ModelAttribute Practise practise, Model model) {
        int correct = 0;

        for(PractiseQuestion q: practise.getPractiseQuestions())
            if(q.getAnswer().equals(q.getChose()))
                correct++;

        model.addAttribute("header", "Done");
        model.addAttribute("subheader", "You have " + correct + " correct answers in 20 random questions.");
        return "simplemessage";

    }
}
