package com.hoanghiep.hust.service;

import com.hoanghiep.hust.dto.CreatePartDto;
import com.hoanghiep.hust.entity.Part;
import com.hoanghiep.hust.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IPartService {

    Page<Part> getAllParts(int pageNo, int pageSize, String sortField, String sortDir);

    Part getPartById(Long id);

    void deletePartById(Long id);

    Part createPart(CreatePartDto createPartDto, String user);

    List<Part> getPartByUnitTestId(Long unitTestId);

    Part getPartByUnitTestIdAndPartNumber(Long unitTestId, int partNumber);
}
