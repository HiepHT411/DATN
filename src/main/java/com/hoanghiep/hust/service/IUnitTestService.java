package com.hoanghiep.hust.service;

import com.hoanghiep.hust.entity.UnitTest;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IUnitTestService {

    Page<UnitTest> getAllUnitTests(int pageNo, int pageSize, String sortField, String sortDir);

    UnitTest getUnitTestById(Long id);

    void deleteUnitTestById(long id);

    void updateUnitTest(long id, UnitTest unitTest);

    Page<UnitTest> getUnitTestsByYear(int pageNo, int pageSize, String sortField, String sortDir, String year);
}
