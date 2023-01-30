package com.hoanghiep.hust.serviceImpl;

import com.hoanghiep.hust.dto.CreatePartDto;
import com.hoanghiep.hust.entity.Part;
import com.hoanghiep.hust.entity.UnitTest;
import com.hoanghiep.hust.entity.User;
import com.hoanghiep.hust.exception.ResourceUnavailableException;
import com.hoanghiep.hust.repository.PartRepo;
import com.hoanghiep.hust.repository.UnitTestRepository;
import com.hoanghiep.hust.service.IPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PartServiceImpl implements IPartService {

    @Autowired
    private PartRepo partRepo;

    @Autowired
    private UnitTestRepository unitTestRepo;

    @Override
    public Page<Part> getAllParts(int pageNo, int pageSize, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return partRepo.findAll(pageable);
    }

    @Override
    public Part getPartById(Long id) {
        return partRepo.findById(id).orElseThrow(()-> new ResourceUnavailableException("part not found with id: "+id));
    }

    @Override
    public void deletePartById(Long id) {
        partRepo.deleteById(id);
    }

    @Override
    public Part createPart(CreatePartDto createPartDto, User user) {
        UnitTest unitTest = new UnitTest();
        Part part = new Part();
        if(Objects.nonNull(unitTestRepo.findByYearAndUnitTestNumber(createPartDto.getYear(), createPartDto.getUnitTestNumber()))){
            unitTest = unitTestRepo.findByYearAndUnitTestNumber(createPartDto.getYear(), createPartDto.getUnitTestNumber());
            if(Objects.nonNull(partRepo.findByUnitTestIdAndPartNumber(unitTest.getId(), createPartDto.getPartNumber()))){
                part = partRepo.findByUnitTestIdAndPartNumber(unitTest.getId(), createPartDto.getPartNumber());
            }
        } else {
            unitTest.setCreatedBy(user.getUsername());
            unitTest.setYear(createPartDto.getYear());
            unitTest.setUnitTestNumber(createPartDto.getUnitTestNumber());
            unitTest.setDescription(createPartDto.getUnitTestDescription());
            unitTest = unitTestRepo.save(unitTest);

            part.setUnitTest(unitTest);
            part.setCreatedBy(user);
            part.setPartNumber(createPartDto.getPartNumber());
            part.setDescription(createPartDto.getPartDescription());
            part.setPartType(createPartDto.getPartType());
            part.setTimes(createPartDto.getTimes());
        }


        return this.partRepo.save(part);
    }

    @Override
    public List<Part> getPartByUnitTestId(Long unitTestId) {
        return partRepo.findByUnitTestId(unitTestId);
    }
}
