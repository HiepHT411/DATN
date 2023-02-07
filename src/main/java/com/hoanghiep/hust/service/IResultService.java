package com.hoanghiep.hust.service;

import com.hoanghiep.hust.entity.Result;
import com.hoanghiep.hust.entity.ResultTest;
import org.springframework.data.domain.Page;

public interface IResultService {

    Page<Result> getTopScorePart(int pageNo, int pageSize, String sortField, String sortDir);

    Page<ResultTest> getTopScoreUnitTest(int pageNo, int pageSize, String sortField, String sortDir);

}
