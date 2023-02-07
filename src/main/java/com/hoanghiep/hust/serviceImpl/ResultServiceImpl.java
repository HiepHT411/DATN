package com.hoanghiep.hust.serviceImpl;

import com.hoanghiep.hust.entity.Result;
import com.hoanghiep.hust.entity.ResultTest;
import com.hoanghiep.hust.repository.ResultRepo;
import com.hoanghiep.hust.repository.ResultTestRepository;
import com.hoanghiep.hust.service.IResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ResultServiceImpl implements IResultService {

    @Autowired
    private ResultRepo resultRepo;

    @Autowired
    private ResultTestRepository resultTestRepository;

    @Override
    public Page<Result> getTopScorePart(int pageNo, int pageSize, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Result> resultPage = resultRepo.findAll(pageable);

        return resultPage;
    }

    @Override
    public Page<ResultTest> getTopScoreUnitTest(int pageNo, int pageSize, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<ResultTest> resultPage = resultTestRepository.findAll(pageable);

        return resultPage;
    }
}
