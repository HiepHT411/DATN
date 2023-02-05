package com.hoanghiep.hust.service;

import com.hoanghiep.hust.entity.Question;
import com.hoanghiep.hust.entity.Result;
import com.hoanghiep.hust.entity.ResultTest;

import java.util.List;

public interface IQuestionService {

    int getResult(List<Question> listQuestion);

    void saveScore(Result result);

    void saveUnitTestResult(ResultTest resultTest);

    void saveQuestion(Question question);
}
