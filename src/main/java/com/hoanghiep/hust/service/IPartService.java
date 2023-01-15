package com.hoanghiep.hust.service;

import com.hoanghiep.hust.entity.Part;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IPartService {

    Page<Part> getAllParts(int pageNo, int pageSize, String sortField, String sortDir);

    Part getPartById(Long id);

    void deletePartById(Long id);

    void createPart(Part part);

    List<Part> getPartByUnitTestId(Long unitTestId);
}
