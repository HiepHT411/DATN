package com.hoanghiep.hust.serviceImpl;

import com.hoanghiep.hust.entity.Part;
import com.hoanghiep.hust.exception.ResourceUnavailableException;
import com.hoanghiep.hust.repository.PartRepo;
import com.hoanghiep.hust.service.IPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartServiceImpl implements IPartService {

    @Autowired
    private PartRepo partRepo;

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
    public void createPart(Part part) {

        this.partRepo.save(part);
    }

    @Override
    public List<Part> getPartByUnitTestId(Long unitTestId) {
        return partRepo.findByUnitTestId(unitTestId);
    }
}
