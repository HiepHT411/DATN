package com.hoanghiep.hust.repository;


import com.hoanghiep.hust.entity.PartDirections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartDirectionsRepository extends JpaRepository<PartDirections, Long> {
}
