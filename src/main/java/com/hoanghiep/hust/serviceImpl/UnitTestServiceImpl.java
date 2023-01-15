package com.hoanghiep.hust.serviceImpl;

import com.hoanghiep.hust.entity.UnitTest;
import com.hoanghiep.hust.exception.ResourceUnavailableException;
import com.hoanghiep.hust.repository.UnitTestRepository;
import com.hoanghiep.hust.service.IUnitTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UnitTestServiceImpl implements IUnitTestService {

    @Autowired
    private UnitTestRepository unitTestRepository;

    @Override
    public Page<UnitTest> getAllUnitTests(int pageNo, int pageSize, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return unitTestRepository.findAll(pageable);
    }

    @Override
    public UnitTest getUnitTestById(Long id) {
        return unitTestRepository.findById(id).orElseThrow(()-> new ResourceUnavailableException("unit test not found by id "+id));
    }
}
