package com.hoanghiep.hust.service;

import com.hoanghiep.hust.entity.Result;
import org.springframework.data.domain.Page;

public interface IResultService {

    Page<Result> getTopScore(int pageNo, int pageSize, String sortField, String sortDir);

}
