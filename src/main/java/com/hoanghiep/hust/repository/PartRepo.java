package com.hoanghiep.hust.repository;

import com.hoanghiep.hust.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepo extends JpaRepository<Part, Long> {

    List<Part> findByUnitTestId(Long id);
}
