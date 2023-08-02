package com.hoanghiep.hust.repository;

import com.hoanghiep.hust.entity.Part;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepo extends JpaRepository<Part, Long> {

    List<Part> findByUnitTestId(Long id);

    Part findByUnitTestIdAndPartNumber(Long id, int partNumber);


    @Query("SELECT new Part(id, partNumber, description, numberOfQuestions, partType, times, unitTest) FROM Part p where (select count(*) from Question q where p.id = q.part.id ) > 0 ")
    Page<Part> findAllPart(Pageable pageable);

    Page<Part> findByPartNumber(Integer partNumber, Pageable pageable);
}
