package com.hoanghiep.hust.serviceImpl;

import com.hoanghiep.hust.entity.Question;
import com.hoanghiep.hust.entity.Result;
import com.hoanghiep.hust.entity.ResultTest;
import com.hoanghiep.hust.repository.ResultRepo;
import com.hoanghiep.hust.repository.ResultTestRepository;
import com.hoanghiep.hust.service.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class QuestionServiceImpl implements IQuestionService {

    @Autowired
    private ResultRepo resultRepo;

    @Autowired
    private ResultTestRepository resultTestRepository;

    @Override
    public int getResult(List<Question> listQuestion) {
        int correct = 0;

        for(Question q: listQuestion)
            if(q.getAns() == q.getChose())
                correct++;

        return correct;
    }

    @Override
    public void saveScore(Result result) {
        Result saveResult = new Result();
        saveResult.setUsername(result.getUsername());
        saveResult.setTotalCorrect(result.getTotalCorrect());
        saveResult.setUnitTestNumber(result.getUnitTestNumber());
        saveResult.setYear(result.getYear());
        saveResult.setPartNumber(result.getPartNumber());
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatDateTime = now.format(format);
        saveResult.setDateTime(formatDateTime);
        resultRepo.save(saveResult);
    }

    @Override
    public void saveUnitTestResult(ResultTest resultTest) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatDateTime = now.format(format);
        resultTest.setDateTime(formatDateTime);
        resultTestRepository.save(resultTest);
    }

}
