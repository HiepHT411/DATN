package com.hoanghiep.hust.repository;

import com.hoanghiep.hust.entity.QuestionStackDirections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionStackDirectionsRepository extends JpaRepository<QuestionStackDirections, Long> {
}
