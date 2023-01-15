package com.hoanghiep.hust.repository;

import com.hoanghiep.hust.entity.ResultTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultTestRepository extends JpaRepository<ResultTest, Integer> {
}
